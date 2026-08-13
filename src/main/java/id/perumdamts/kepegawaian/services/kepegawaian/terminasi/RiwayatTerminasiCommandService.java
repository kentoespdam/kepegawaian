package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.*;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatKontrak.RiwayatKontrakMapper;
import id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatMutasi.RiwayatMutasiMapper;
import id.perumdamts.kepegawaian.mapper.kepegawaian.riwayatSk.RiwayatSkMapper;
import id.perumdamts.kepegawaian.mapper.kepegawaian.terminasi.RiwayatTerminasiMapper;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.*;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlasanBerhentiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkCommandService;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiWriteback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiwayatTerminasiCommandService {
    private final RiwayatTerminasiRepository repository;
    private final AlasanBerhentiRepository alasanBerhentiRepository;
    private final RiwayatSkRepository skRepository;
    private final GolonganRepository golonganRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final RiwayatMutasiRepository riwayatMutasiRepository;
    private final RiwayatKontrakRepository kontrakRepository;
    private final LampiranSkCommandService lampiranSkCommandService;
    private final LampiranSkRepository lampiranSkRepository;
    private final PegawaiWriteback pegawaiWriteback;
    private final AuthService authService;

    @Transactional(rollbackFor = Exception.class)
    public RiwayatTerminasi save(RiwayatTerminasiPostRequest request) {
        boolean exists = repository.exists(request.getTerminasiSpecification());
        if (exists) {
            throw new ConflictException("Terminasi is already exist");
        }

        AlasanBerhenti alasanBerhenti = alasanBerhentiRepository.findById(request.getAlasanTerminasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Alasan Terminasi"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));

        // 1. Save SK
        RiwayatSk skEntity = RiwayatSkMapper.toEntity(request, pegawai, golongan);
        RiwayatSk savedSk = skRepository.save(skEntity);

        // Update Pegawai
        pegawai.setStatusKerja(EStatusKerja.BERHENTI_OR_KELUAR);
        pegawaiWriteback.savePegawai(pegawai);

        // ADR-0039: pegawai keluar → user Appwrite di-disable (best-effort, tidak di-hard-delete)
        authService.blockUserIfExists(pegawai.getId().toString());

        // Add attachment if provided
        if (request.getFileName() != null) {
            LampiranSkPostRequest lampRequest = LampiranSkPostRequest.builder()
                    .ref(EJenisSk.SK_PENSIUN)
                    .refId(savedSk.getId())
                    .fileName(request.getFileName())
                    .notes(request.getNotes())
                    .build();
            lampiranSkCommandService.addLampiran(lampRequest);
        }

        // 2. Save Terminasi
        RiwayatTerminasi terminasi = RiwayatTerminasiMapper.toEntity(request, alasanBerhenti, savedSk, golongan, jabatan, organisasi);
        RiwayatTerminasi savedTerminasi = repository.save(terminasi);

        // 3. Save Mutasi
        RiwayatMutasi riwayatMutasi = RiwayatMutasiMapper.toEntity(savedTerminasi);
        riwayatMutasiRepository.save(riwayatMutasi);

        // 4. Save Kontrak if pegawai status is KONTRAK
        if (pegawai.getStatusPegawai() == EStatusPegawai.KONTRAK) {
            RiwayatKontrak kontrakEntity = RiwayatKontrakMapper.toEntity(request, pegawai);
            kontrakEntity.setRiwayatSk(savedSk);
            RiwayatKontrak savedKontrak = kontrakRepository.save(kontrakEntity);
            updateKontrakLatest(savedKontrak);
        }

        return savedTerminasi;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatTerminasi update(Long id, RiwayatTerminasiPutRequest request) {
        boolean exists = repository.exists(request.getTerminasiSpecification()
                .and((root, query, cb) -> cb.notEqual(root.get("id"), id)));
        if (exists) {
            throw new ConflictException("Terminasi is already exist");
        }
        RiwayatTerminasi terminasi = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Riwayat Terminasi"));
        AlasanBerhenti alasanTerminasi = alasanBerhentiRepository.findById(request.getAlasanTerminasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Alasan Terminasi"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));

        Golongan golongan = golonganRepository.findById(request.getGolonganId())
                .orElse(null);
        Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                .orElseThrow(() -> new NotFoundException("Unknown Organisasi"));
        Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                .orElseThrow(() -> new NotFoundException("Unknown Jabatan"));

        // Update SK
        RiwayatSk skTerminasi = terminasi.getSkTerminasi();
        skTerminasi.setPegawai(pegawai);
        skTerminasi.setNomorSk(request.getNomorSk());
        skTerminasi.setTanggalSk(request.getTanggalSk());
        skTerminasi.setTmtBerlaku(request.getTmtBerlaku());
        if (golongan != null) {
            skTerminasi.setGolongan(golongan);
        }
        skTerminasi.setNotes(request.getNotes());
        RiwayatSk savedSk = skRepository.save(skTerminasi);

        // Attachment update
        if (request.getFileName() != null) {
            var oldLampirans = lampiranSkRepository.findByRefAndRefId(EJenisSk.SK_PENSIUN, savedSk.getId());
            if (!oldLampirans.isEmpty()) {
                LampiranSk oldLampiran = oldLampirans.getFirst();
                if (oldLampiran != null) {
                    lampiranSkCommandService.deleteById(oldLampiran.getId());
                }
            }

            LampiranSkPostRequest lampRequest = LampiranSkPostRequest.builder()
                    .ref(EJenisSk.SK_PENSIUN)
                    .refId(savedSk.getId())
                    .fileName(request.getFileName())
                    .notes(request.getNotes())
                    .build();
            lampiranSkCommandService.addLampiran(lampRequest);
        }

        // Update Terminasi
        RiwayatTerminasi entity = RiwayatTerminasiMapper.updateEntity(request, terminasi, alasanTerminasi, savedSk, golongan, jabatan, organisasi);
        return repository.save(entity);
    }

    private void updateKontrakLatest(RiwayatKontrak entity) {
        Specification<RiwayatKontrak> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), entity.getPegawai().getId()),
                criteriaBuilder.notEqual(root.get("id"), entity.getId())
        );
        kontrakRepository.findAll(specification).stream().peek(k -> k.setIsLatest(false)).forEach(kontrakRepository::save);
    }
}
