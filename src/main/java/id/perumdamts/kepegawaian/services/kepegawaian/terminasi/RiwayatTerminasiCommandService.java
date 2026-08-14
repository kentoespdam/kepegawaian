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
import id.perumdamts.kepegawaian.mapper.kepegawaian.terminasi.RiwayatTerminasiMapper;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.*;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlasanBerhentiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkCommandService;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiWriteback;
import lombok.RequiredArgsConstructor;
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
    private final RiwayatSkCommandService skCommandService;
    private final RiwayatMutasiCommandService mutasiCommandService;
    private final RiwayatKontrakCommandService kontrakCommandService;
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

        // 1. SK terminasi — tulis milik aggregate SK (guard anti-duplikat ikut aktif, ADR-0034)
        RiwayatSk savedSk = skCommandService.save(request);

        // 2. Lifecycle pegawai (ADR-0039): status BERHENTI + user Appwrite di-disable (best-effort)
        pegawai.setStatusKerja(EStatusKerja.BERHENTI_OR_KELUAR);
        pegawaiWriteback.savePegawai(pegawai);
        authService.blockUserIfExists(pegawai.getId().toString());

        // 3. Lampiran SK pensiun (opsional)
        if (request.getFileName() != null) {
            LampiranSkPostRequest lampRequest = LampiranSkPostRequest.builder()
                    .ref(EJenisSk.SK_PENSIUN)
                    .refId(savedSk.getId())
                    .fileName(request.getFileName())
                    .notes(request.getNotes())
                    .build();
            lampiranSkCommandService.addLampiran(lampRequest);
        }

        // 4. Terminasi
        RiwayatTerminasi terminasi = RiwayatTerminasiMapper.toEntity(request, alasanBerhenti, savedSk, golongan, jabatan, organisasi);
        RiwayatTerminasi savedTerminasi = repository.save(terminasi);

        // 5. Mutasi terminasi — tulis milik aggregate mutasi
        mutasiCommandService.createFromTerminasi(savedTerminasi);

        // 6. Kontrak terminasi (khusus pegawai KONTRAK) — tulis milik aggregate kontrak
        if (pegawai.getStatusPegawai() == EStatusPegawai.KONTRAK) {
            kontrakCommandService.createForTerminasi(request, pegawai, savedSk);
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

}
