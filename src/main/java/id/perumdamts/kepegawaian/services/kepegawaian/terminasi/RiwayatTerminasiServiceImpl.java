package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatTerminasiRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.GenericSkService;
import id.perumdamts.kepegawaian.utils.DetailFromList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiwayatTerminasiServiceImpl implements RiwayatTerminasiService {
    private final RiwayatTerminasiRepository repository;
    private final GenericSkService skService;
    private final GolonganRepository golonganRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;

    @Override
    public Page<RiwayatTerminasiResponse> findPage(RiwayatTerminasiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatTerminasiResponse::from);
    }

    @Override
    public RiwayatTerminasiResponse findById(Long id) {
        return repository.findById(id).map(RiwayatTerminasiResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(RiwayatTerminasiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getTerminasiSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Terminasi is already exist");

            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

            List<Golongan> golonganList = golonganRepository.findAll();
            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();

            Golongan golongan = DetailFromList.findExistGolongan(golonganList, request.getGolonganId());
            Organisasi organisasi = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiId());
            if (organisasi == null) throw new RuntimeException("Unknown Organisasi");
            Jabatan jabatan = DetailFromList.findExistJabatan(jabatanList, request.getJabatanId());
            if (jabatan == null) throw new RuntimeException("Unknown Jabatan");

            RiwayatSk riwayatSk = skService.saveSkTerminasi(request, pegawai, golongan);
            RiwayatTerminasi entity = RiwayatTerminasiPostRequest.toEntity(request, riwayatSk, golongan, jabatan, organisasi);
            RiwayatTerminasi save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatTerminasiPutRequest request) {
        try {
            RiwayatTerminasi terminasi = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Terminasi"));

            List<Golongan> golonganList = golonganRepository.findAll();
            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();

            Golongan golongan = DetailFromList.findExistGolongan(golonganList, request.getGolonganId());
            Organisasi organisasi = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiId());
            if (organisasi == null) throw new RuntimeException("Unknown Organisasi");
            Jabatan jabatan = DetailFromList.findExistJabatan(jabatanList, request.getJabatanId());
            if (jabatan == null) throw new RuntimeException("Unknown Jabatan");

            RiwayatSk riwayatSk = skService.updateTerminasi(request, terminasi.getSkTerminasi(), golongan);
            RiwayatTerminasi entity = RiwayatTerminasiPutRequest.toEntity(terminasi, riwayatSk, golongan, jabatan, organisasi);
            RiwayatTerminasi save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
