package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.LampiranSkRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatSkRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkService;
import id.perumdamts.kepegawaian.services.pegawai.GenericPegawaiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericSkService {
    private final RiwayatSkRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GenericPegawaiService pegawaiService;
    private final LampiranSkService lampiranSkService;
    private final LampiranSkRepository lampiranSkRepository;

    public RiwayatSk saveSkGolongan(RiwayatSkPostRequest request, Golongan golongan) {
        this.mainValidate(request);
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai, golongan);
        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateGolongan(pegawai, save);

        return save;
    }

    public RiwayatSk updateSkGolongan(RiwayatMutasi riwayatMutasi, RiwayatMutasiPutRequest request, Golongan golonganBaru) {
        RiwayatSk riwayatSk = riwayatMutasi.getRiwayatSk();
        RiwayatSkPutRequest.toEntity(riwayatSk, request, golonganBaru);
        RiwayatSk save = this.saveSK(riwayatSk);
        pegawaiService.updateGolongan(riwayatMutasi.getPegawai(), save);
        return save;
    }

    public RiwayatSk saveSkJabatan(
            RiwayatMutasiPostRequest request,
            Organisasi organisasiBaru,
            Jabatan jabatanBaru,
            Profesi profesiBaru
    ) {
        this.mainValidate(request);
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

        RiwayatSk entity = Objects.isNull(pegawai.getGolongan()) ?
                RiwayatSkPostRequest.toEntity(request, pegawai) :
                RiwayatSkPostRequest.toEntity(request, pegawai, pegawai.getGolongan());
        RiwayatSk riwayatSk = this.saveSK(entity);
        pegawaiService.updateJabatan(pegawai, riwayatSk, organisasiBaru, jabatanBaru, profesiBaru);

        return riwayatSk;
    }

    public RiwayatSk updateSkJabatan(
            RiwayatMutasi riwayatMutasi,
            RiwayatMutasiPutRequest request,
            Organisasi organisasiBaru,
            Jabatan jabatanBaru,
            Profesi profesiBaru
    ) {
        RiwayatSk riwayatSk = riwayatMutasi.getRiwayatSk();
        RiwayatSk entity = RiwayatSkPutRequest.toEntity(riwayatSk, request);
        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateJabatan(riwayatMutasi.getPegawai(), save, organisasiBaru, jabatanBaru, profesiBaru);
        return save;
    }

    public void saveKontrak(RiwayatKontrakPostRequest request, Pegawai pegawai) {
        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorKontrak());
        entity.setJenisSk(EJenisSk.SK_LAINNYA);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTanggalMulai());
        entity.setGajiPokok(request.getGajiPokok());
        entity.setNotes(request.getNotes());

        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateKontrak(pegawai, save);
    }

    public void saveSkKontrakFromPegawai(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(EJenisSk.SK_LAINNYA);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlakuSk());
        entity.setGajiPokok(request.getGajiPokok());
        entity.setNotes(request.getNotes());

        this.saveSK(entity);
    }

    public void saveKontrakToCapeg(RiwayatKontrakPostRequest request, Pegawai pegawai, Golongan golongan) {
        pegawai.setGolongan(golongan);
        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(request.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorKontrak());
        entity.setJenisSk(EJenisSk.SK_CAPEG);
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTanggalMulai());
        entity.setGolongan(golongan);
        entity.setMkgbBulan(0);
        entity.setMkgTahun(0);
        entity.setGajiPokok(request.getGajiPokok());
        entity.setNotes(request.getNotes());

        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateGolongan(pegawai, save);
    }

    public RiwayatSk saveSkTerminasi(RiwayatTerminasiPostRequest request, Pegawai pegawai, Golongan golongan) {
        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai, golongan);
        RiwayatSk riwayatSk = this.saveSK(entity);
        pegawai.setStatusKerja(EStatusKerja.BERHENTI_OR_KELUAR);
        pegawaiService.updatePegawai(pegawai);
        if (request.getFileName() != null) {
            LampiranSkPostRequest lampiranSkPostRequest = new LampiranSkPostRequest();
            lampiranSkPostRequest.setRef(EJenisSk.SK_PENSIUN);
            lampiranSkPostRequest.setRefId(riwayatSk.getId());
            lampiranSkPostRequest.setFileName(request.getFileName());
            lampiranSkPostRequest.setNotes(request.getNotes());
            lampiranSkService.addLampiran(lampiranSkPostRequest);
        }
        return riwayatSk;
    }

    public RiwayatSk updateTerminasi(RiwayatTerminasiPutRequest request, RiwayatTerminasi terminasi, Pegawai pegawai, Golongan golongan) {
        try {
            RiwayatSk skTerminasi = terminasi.getSkTerminasi();
            skTerminasi.setPegawai(pegawai);
            skTerminasi.setNomorSk(request.getNomorSk());
            skTerminasi.setTanggalSk(request.getTanggalSk());
            skTerminasi.setTmtBerlaku(request.getTmtBerlaku());
            if (golongan != null)
                skTerminasi.setGolongan(golongan);
            skTerminasi.setNotes(request.getNotes());
            if (request.getFileName() != null) {
                LampiranSk oldLampiran = lampiranSkRepository.findByRefAndRefId(EJenisSk.SK_PENSIUN, skTerminasi.getId()).getFirst();
                if (oldLampiran != null)
                    lampiranSkService.deleteById(oldLampiran.getId());

                LampiranSkPostRequest lampiranSkPostRequest = new LampiranSkPostRequest();
                lampiranSkPostRequest.setRef(EJenisSk.SK_PENSIUN);
                lampiranSkPostRequest.setRefId(skTerminasi.getId());
                lampiranSkPostRequest.setFileName(request.getFileName());
                lampiranSkPostRequest.setNotes(request.getNotes());
                lampiranSkService.addLampiran(lampiranSkPostRequest);
            }
            return repository.save(skTerminasi);
        } catch (Exception e) {
            log.info("updateTerminasi: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void mainValidate(RiwayatSkPostRequest request) {
        if (request.getTmtBerlaku().isBefore(request.getTmtBerlaku()))
            throw new RuntimeException("TMT Berlaku cannot before TMT Berlaku");

        boolean exists = repository.exists(request.getSpecification());
        if (exists) throw new RuntimeException("Riwayat SK is Exists");
    }


    private RiwayatSk saveSK(RiwayatSk entity) {
        return repository.save(entity);
    }

    public void delete(RiwayatKontrak riwayatKontrak) {
        Specification<RiwayatSk> specification = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("pegawai").get("id"), riwayatKontrak.getPegawai().getId()),
                        criteriaBuilder.equal(root.get("nomorSk"), riwayatKontrak.getNomorKontrak())
                )
        );
        repository.findAll(specification).stream().map(r -> {
            r.setIsDeleted(true);
            return repository.save(r).getId();
        }).forEach(lampiranSkService::deleteByRefId);
    }
}
