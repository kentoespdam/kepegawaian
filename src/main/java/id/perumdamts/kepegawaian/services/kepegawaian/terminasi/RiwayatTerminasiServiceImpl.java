package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatTerminasiRepository;
import id.perumdamts.kepegawaian.repositories.master.AlasanBerhentiRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.lampiran.LampiranSkService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.GenericSkService;
import id.perumdamts.kepegawaian.utils.DetailFromList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiwayatTerminasiServiceImpl implements RiwayatTerminasiService {
    private final RiwayatTerminasiRepository repository;
    private final AlasanBerhentiRepository alasanBerhentiRepository;
    private final GenericSkService skService;
    private final GolonganRepository golonganRepository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final LampiranSkService lampiranSkService;

    @Override
    public Page<RiwayatTerminasiResponse> findPage(RiwayatTerminasiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(riwayatTerminasi -> {
                    List<LampiranSkResponse> lampiran = lampiranSkService.getLampiran(
                            EJenisSk.SK_PENSIUN,
                            riwayatTerminasi.getSkTerminasi().getId()
                    );
                    return RiwayatTerminasiResponse.from(riwayatTerminasi, lampiran);
                });
    }

    @Override
    public Page<PegawaiResponse> findPageCalonPensiun(RiwayatTerminasiRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusMonths(3);
        request.setTanggalTerminasi(end);
        request.setSortBy("Biodata.nama");
        request.setSortDirection("ASC");

        return pegawaiRepository.findAll(request.getCalonPensiunSpecification(), request.getPageable())
                .map(PegawaiResponse::from);
    }

    @Override
    public RiwayatTerminasiResponse findById(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    List<LampiranSkResponse> lampiran = lampiranSkService.getLampiran(EJenisSk.SK_PENSIUN, entity.getSkTerminasi().getId());
                    return RiwayatTerminasiResponse.from(entity, lampiran);
                }).orElse(null);
    }

    @Override
    public SavedStatus<?> save(RiwayatTerminasiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getTerminasiSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Terminasi is already exist");

            AlasanBerhenti alasanBerhenti = alasanBerhentiRepository.findById(request.getAlasanTerminasiId()).orElseThrow(() -> new RuntimeException("Unknown Alasan Terminasi"));
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
            RiwayatTerminasi entity = RiwayatTerminasiPostRequest.toEntity(request, alasanBerhenti, riwayatSk, golongan, jabatan, organisasi);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Terminasi pegawai berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatTerminasiPutRequest request) {
        try {
            RiwayatTerminasi terminasi = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Riwayat Terminasi"));
            AlasanBerhenti alasanTerminasi = alasanBerhentiRepository.findById(request.getAlasanTerminasiId()).orElseThrow(() -> new RuntimeException("Unknown Alasan Terminasi"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));

            List<Golongan> golonganList = golonganRepository.findAll();
            List<Organisasi> organisasiList = organisasiRepository.findAll();
            List<Jabatan> jabatanList = jabatanRepository.findAll();

            Golongan golongan = DetailFromList.findExistGolongan(golonganList, request.getGolonganId());
            Organisasi organisasi = DetailFromList.findExistOrganisasi(organisasiList, request.getOrganisasiId());
            if (organisasi == null) throw new RuntimeException("Unknown Organisasi");
            Jabatan jabatan = DetailFromList.findExistJabatan(jabatanList, request.getJabatanId());
            if (jabatan == null) throw new RuntimeException("Unknown Jabatan");

            RiwayatSk riwayatSk = skService.updateTerminasi(request, terminasi, pegawai, golongan);
            RiwayatTerminasi entity = RiwayatTerminasiPutRequest.toEntity(request, terminasi, alasanTerminasi, riwayatSk, golongan, jabatan, organisasi);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Terminasi pegawai berhasil diupdate");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
