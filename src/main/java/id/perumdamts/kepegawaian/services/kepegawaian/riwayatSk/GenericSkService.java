package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatSkRepository;
import id.perumdamts.kepegawaian.services.pegawai.GenericPegawaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericSkService {
    private final RiwayatSkRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final GenericPegawaiService pegawaiService;

    public RiwayatSk saveSkGolongan(RiwayatSkPostRequest request, Golongan golongan) {
        this.mainValidate(request);
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai, golongan);
        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateGolongan(pegawai, save, golongan);

        return save;
    }

    public RiwayatSk updateSkGolongan(RiwayatMutasi riwayatMutasi, RiwayatMutasiPutRequest request, Golongan golonganBaru) {
        RiwayatSk riwayatSk = riwayatMutasi.getRiwayatSk();
        RiwayatSkPutRequest.toEntity(riwayatSk, request, golonganBaru);
        RiwayatSk save = this.saveSK(riwayatSk);
        pegawaiService.updateGolongan(riwayatMutasi.getPegawai(), save, golonganBaru);
        return save;
    }

    public RiwayatSk saveSkJabatan(RiwayatMutasiPostRequest request) {
        this.mainValidate(request);
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai, pegawai.getGolongan());
        RiwayatSk riwayatSk = this.saveSK(entity);
        pegawaiService.updateJabatan(pegawai, riwayatSk);

        return riwayatSk;
    }

    public RiwayatSk updateSkJabatan(RiwayatMutasi riwayatMutasi, RiwayatMutasiPutRequest request) {
        RiwayatSk riwayatSk = riwayatMutasi.getRiwayatSk();
        RiwayatSk entity = RiwayatSkPutRequest.toEntity(riwayatSk, request);
        RiwayatSk save = this.saveSK(entity);
        pegawaiService.updateJabatan(riwayatMutasi.getPegawai(), save);
        return save;
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
}
