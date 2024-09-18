package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
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
            request.setJenisSk(EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN);
            boolean exists = repository.exists(request.getSpecificationMutasi());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Mutasi is Exists");
            List<Golongan> golonganList = golonganRepository.findAll();
            Golongan golonganBaru = findExistGolongan(golonganList, request.getGolonganId());
            Golongan golonganLama = findExistGolongan(golonganList, request.getGolonganLamaId());

            RiwayatSk riwayatSk = skService.saveSkGolongan(request, golonganBaru);
            RiwayatMutasi entity = RiwayatMutasiPostRequest.toEntity(request, riwayatSk, golonganBaru, golonganLama);

            RiwayatMutasi save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    public SavedStatus<?> saveJabatan(RiwayatMutasiPostRequest request) {
        try {
            request.setJenisSk(EJenisSk.SK_JABATAN);
            boolean exists = repository.exists(request.getSpecificationMutasi());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Riwayat Mutasi is Exists");
            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();
            List<Profesi> profesiList = profesiRepository.findAll();

            Organisasi organisasiBaru = findExistOrganisasi(organisasiList, request.getOrganisasiId());
            Organisasi organisasiLama = findExistOrganisasi(organisasiList, request.getOrganisasiLamaId());
            Jabatan jabatanBaru = findExistJabatan(jabatanList, request.getJabatanId());
            Jabatan jabatanLama = findExistJabatan(jabatanList, request.getJabatanLamaId());
            Profesi profesiBaru = findExistProfesi(profesiList, request.getProfesiId());
            Profesi profesiLama = findExistProfesi(profesiList, request.getProfesiLamaId());

            RiwayatSk riwayatSk = skService.saveSkJabatan(request);

            RiwayatMutasi entity = RiwayatMutasiPostRequest.toEntity(request, riwayatSk, organisasiBaru, jabatanBaru, profesiBaru, organisasiLama, jabatanLama, profesiLama);

            RiwayatMutasi save = repository.save(entity);

            return SavedStatus.build(ESaveStatus.SUCCESS, save);

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    private Golongan findExistGolongan(List<Golongan> golonganList, Long golonganId) {
        return golonganList.stream()
                .filter(golongan -> golongan.getId().equals(golonganId))
                .findFirst()
                .orElseGet(() -> golonganRepository.findById(golonganId)
                        .orElseThrow(() -> new RuntimeException("Unknown Golongan")));
    }

    private Organisasi findExistOrganisasi(List<Organisasi> organisasiList, Long organisasiId) {
        return organisasiList.stream()
                .filter(organisasi -> organisasi.getId().equals(organisasiId))
                .findFirst()
                .orElseGet(() -> organisasiRepository.findById(organisasiId)
                        .orElseThrow(() -> new RuntimeException("Unknown Organisasi")));
    }

    private Jabatan findExistJabatan(List<Jabatan> jabatanList, Long jabatanId) {
        return jabatanList.stream()
                .filter(jabatan -> jabatan.getId().equals(jabatanId))
                .findFirst()
                .orElseGet(() -> jabatanRepository.findById(jabatanId)
                        .orElseThrow(() -> new RuntimeException("Unknown Jabatan")));
    }

    private Profesi findExistProfesi(List<Profesi> profesiList, Long profesiId) {
        return profesiList.stream()
                .filter(profesi -> profesi.getId().equals(profesiId))
                .findFirst()
                .orElseGet(() -> profesiRepository.findById(profesiId)
                        .orElseThrow(() -> new RuntimeException("Unknown Profesi")));
    }
}