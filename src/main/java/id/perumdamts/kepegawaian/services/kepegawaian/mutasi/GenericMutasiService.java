package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.GenericSkService;
import id.perumdamts.kepegawaian.utils.DetailFromList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenericMutasiService {
    private final RiwayatMutasiRepository repository;
    private final GenericSkService skService;
    private final GolonganRepository golonganRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final ProfesiRepository profesiRepository;

    public SavedStatus<?> saveGolongan(RiwayatMutasiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecificationMutasi());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Mutasi is Exists");
            List<Golongan> golonganList = golonganRepository.findAll();
            Golongan golonganBaru = DetailFromList.findExistGolongan(golonganList, request.getGolonganId());
            if (golonganBaru == null) throw new RuntimeException("Unknown Golongan");
            Golongan golonganLama = DetailFromList.findExistGolongan(golonganList, request.getGolonganLamaId());
            if (golonganLama == null) throw new RuntimeException("Unknown Golongan");

            RiwayatSk riwayatSk = skService.saveSkGolongan(request, golonganBaru);
            RiwayatMutasi entity = RiwayatMutasiPostRequest.toEntity(request, riwayatSk, golonganBaru, golonganLama);

            RiwayatMutasi save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    public SavedStatus<?> updateGolongan(Long id, RiwayatMutasiPutRequest request) {
        try {
            RiwayatMutasi riwayatMutasi = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Mutasi"));
            List<Golongan> golonganList = golonganRepository.findAll();
            Golongan golonganBaru = DetailFromList.findExistGolongan(golonganList, request.getGolonganId());
            if (golonganBaru == null) throw new RuntimeException("Unknown Golongan");
            Golongan golonganLama = DetailFromList.findExistGolongan(golonganList, request.getGolonganLamaId());
            if (golonganLama == null) throw new RuntimeException("Unknown Golongan");
            RiwayatSk riwayatSk = skService.updateSkGolongan(riwayatMutasi, request, golonganBaru);
            RiwayatMutasi entity = RiwayatMutasiPutRequest.toEntity(riwayatMutasi, riwayatSk, request, golonganBaru, golonganLama);

            RiwayatMutasi save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    public SavedStatus<?> saveJabatan(RiwayatMutasiPostRequest request) {
        try {
            if (repository.exists(request.getSpecificationMutasi())) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Mutasi is already Exists");
            }

            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();
            List<Profesi> profesiList = profesiRepository.findAll();

            Organisasi organisasiBaru = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiId());
            if (organisasiBaru == null) throw new RuntimeException("Unknown Organisasi");
            Organisasi organisasiLama = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiLamaId());
            if (organisasiLama == null) throw new RuntimeException("Unknown Organisasi");
            Jabatan jabatanBaru = DetailFromList.findExistJabatan(jabatanList, request.getJabatanId());
            if (jabatanBaru == null) throw new RuntimeException("Unknown Jabatan");
            Jabatan jabatanLama = DetailFromList.findExistJabatan(jabatanList, request.getJabatanLamaId());
            if (jabatanLama == null) throw new RuntimeException("Unknown Jabatan");
            Profesi profesiBaru = DetailFromList.findExistProfesi(profesiList, request.getProfesiId());
            if (profesiBaru == null) throw new RuntimeException("Unknown Profesi");
            Profesi profesiLama = DetailFromList.findExistProfesi(profesiList, request.getProfesiLamaId());
            if (profesiLama == null) throw new RuntimeException("Unknown Profesi");

            RiwayatSk riwayatSk = skService.saveSkJabatan(request, organisasiBaru, jabatanBaru, profesiBaru);

            RiwayatMutasi entity = RiwayatMutasiPostRequest.toEntity(request, riwayatSk, organisasiBaru, jabatanBaru, profesiBaru, organisasiLama, jabatanLama, profesiLama);

            RiwayatMutasi save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    public SavedStatus<?> updateJabatan(Long id, RiwayatMutasiPutRequest request) {
        try {
            RiwayatMutasi riwayatMutasi = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Mutasi"));
            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();
            List<Profesi> profesiList = profesiRepository.findAll();

            Organisasi organisasiBaru = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiId());
            if (organisasiBaru == null) throw new RuntimeException("Unknown Organisasi");
            Organisasi organisasiLama = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiLamaId());
            if (organisasiLama == null) throw new RuntimeException("Unknown Organisasi");
            Jabatan jabatanBaru = DetailFromList.findExistJabatan(jabatanList, request.getJabatanId());
            if (jabatanBaru == null) throw new RuntimeException("Unknown Jabatan");
            Jabatan jabatanLama = DetailFromList.findExistJabatan(jabatanList, request.getJabatanLamaId());
            if (jabatanLama == null) throw new RuntimeException("Unknown Jabatan");
            Profesi profesiBaru = DetailFromList.findExistProfesi(profesiList, request.getProfesiId());
            if (profesiBaru == null) throw new RuntimeException("Unknown Profesi");
            Profesi profesiLama = DetailFromList.findExistProfesi(profesiList, request.getProfesiLamaId());
            if (profesiLama == null) throw new RuntimeException("Unknown Profesi");

            RiwayatSk riwayatSk = skService.updateSkJabatan(
                    riwayatMutasi,
                    request,
                    organisasiBaru,
                    jabatanBaru,
                    profesiBaru
            );
            RiwayatMutasi entity = RiwayatMutasiPutRequest.toEntity(
                    riwayatMutasi,
                    riwayatSk,
                    request,
                    organisasiBaru,
                    jabatanBaru,
                    profesiBaru,
                    organisasiLama,
                    jabatanLama,
                    profesiLama
            );
            RiwayatMutasi save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }


    public SavedStatus<?> save(RiwayatMutasiPostRequest request) {
        return switch (request.getJenisMutasi()) {
            case MUTASI_GOLONGAN, MUTASI_GAJI, MUTASI_GAJI_BERKALA -> saveGolongan(request);
            case MUTASI_JABATAN, MUTASI_LOKER -> saveJabatan(request);
            default -> SavedStatus.build(ESaveStatus.FAILED, "Unknown Mutasi");
        };
    }

    public SavedStatus<?> update(Long id, RiwayatMutasiPutRequest request) {
        return switch (request.getJenisMutasi()) {
            case MUTASI_GOLONGAN, MUTASI_GAJI, MUTASI_GAJI_BERKALA -> updateGolongan(id, request);
            case MUTASI_JABATAN, MUTASI_LOKER -> updateJabatan(id, request);
            default -> SavedStatus.build(ESaveStatus.FAILED, "Unknown Mutasi");
        };
    }
}
