package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.LampiranSkRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatTerminasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlasanBerhentiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatKontrakRepository;
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
        RiwayatSk skEntity = RiwayatSkPostRequest.toEntity(request, pegawai, golongan);
        RiwayatSk savedSk = skRepository.save(skEntity);

        // Update Pegawai
        pegawai.setStatusKerja(EStatusKerja.BERHENTI_OR_KELUAR);
        pegawaiWriteback.savePegawai(pegawai);

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
        RiwayatTerminasi terminasi = RiwayatTerminasiPostRequest.toEntity(request, alasanBerhenti, savedSk, golongan, jabatan, organisasi);
        RiwayatTerminasi savedTerminasi = repository.save(terminasi);

        // 3. Save Mutasi
        RiwayatMutasi riwayatMutasi = RiwayatMutasiPostRequest.toEntity(savedTerminasi);
        riwayatMutasiRepository.save(riwayatMutasi);

        // 4. Save Kontrak if pegawai status is KONTRAK
        if (pegawai.getStatusPegawai() == EStatusPegawai.KONTRAK) {
            RiwayatKontrak kontrakEntity = RiwayatKontrakPostRequest.toEntity(request, pegawai);
            RiwayatKontrak savedKontrak = kontrakRepository.save(kontrakEntity);
            updateKontrakLatest(savedKontrak);
        }

        return savedTerminasi;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiwayatTerminasi update(Long id, RiwayatTerminasiPutRequest request) {
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
                LampiranSk oldLampiran = oldLampirans.get(0);
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
        RiwayatTerminasi entity = RiwayatTerminasiPutRequest.toEntity(request, terminasi, alasanTerminasi, savedSk, golongan, jabatan, organisasi);
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
