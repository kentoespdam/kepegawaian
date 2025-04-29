package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.master.*;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.GenericKontrakService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PegawaiServiceImpl implements PegawaiService {
    private final PegawaiRepository repository;
    private final BiodataRepository biodataRepository;
    private final JabatanRepository jabatanRepository;
    private final OrganisasiRepository organisasiRepository;
    private final ProfesiRepository profesiRepository;
    private final GolonganRepository golonganRepository;
    private final BiodataService biodataService;
    private final RiwayatSkService riwayatSkService;
    private final GenericKontrakService genericKontrakService;
    private final GajiPendapatanNonPajakRepository gajiPendapatanNonPajakRepository;
    private final GajiProfilRepository gajiProfilRepository;
    private final RumahDinasRepository rumahDinasRepository;
    private final AuthService authService;
    private final RiwayatSkRepository riwayatSkRepository;

    private static final EStatusPegawai[] EXCLUDED_GOLONGAN_STATUSES = {
            EStatusPegawai.KONTRAK,
            EStatusPegawai.CALON_HONORER,
            EStatusPegawai.HONORER
    };
    private static final Long[] EXCLUDED_JABATAN_IDS = {1L, 2L, 3L, 4L};


    @Override
    public Page<PegawaiResponse> findPage(PegawaiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PegawaiResponse::from);
    }

    @Override
    public List<PegawaiListResponse> findAll(PegawaiRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(PegawaiListResponse::from).toList();
    }

    @Override
    public PegawaiResponseDetail findById(Long id) {
        return repository.findById(id).map(pegawai -> {
            List<RiwayatSk> list = riwayatSkRepository.findByPegawai_IdOrderByTmtBerlakuDesc(pegawai.getId());
            return PegawaiResponseDetail.from(pegawai, list);
        }).orElse(null);
    }

    @Override
    public PegawaiResponse findByNipam(String nipam) {
        return repository.findOneByNipam(nipam).map(PegawaiResponse::from).orElse(null);
    }

    @Override
    public PegawaiResponseRingkasan findRingkasan(Long id) {
        return repository.findById(id)
                .map(PegawaiResponseRingkasan::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(PegawaiPostRequest request) {
        try {
            Optional<Pegawai> oneByNipam = repository.findOneByNipam(request.getNipam());
            if (oneByNipam.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Pegawai is Exist");
            Biodata biodata = biodataRepository.findById(request.getNik())
                    .orElseGet(() -> biodataService.saveFromPegawai(request));
            if (request.getStatusPegawai().equals(EStatusPegawai.NON_PEGAWAI))
                return SavedStatus.build(ESaveStatus.SUCCESS, biodata);

            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Profesi profesi = request.getProfesiId() == null ? null : profesiRepository.findById(request.getProfesiId())
                    .orElse(null);
            Golongan golongan = request.getGolonganId() == null || ArrayUtils.contains(EXCLUDED_GOLONGAN_STATUSES, request.getStatusPegawai()) ||
                    ArrayUtils.contains(EXCLUDED_JABATAN_IDS, request.getJabatanId()) ? null :
                    golonganRepository.findById(request.getGolonganId())
                            .orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository
                    .findById(request.getKodePajakId()).orElseThrow(() -> new RuntimeException("Unknown Kode Pajak"));

            Pegawai entity = PegawaiPostRequest.toEntity(request, biodata, jabatan, organisasi, profesi, golongan, kodePajak);
            Pegawai pegawai = repository.save(entity);

            switch (request.getStatusPegawai()) {
                case KONTRAK:
                    genericKontrakService.saveFromPegawai(request, pegawai);
                    break;
                case HONORER:
                case PEGAWAI:
                    RiwayatSk riwayatSk = riwayatSkService.savePegawai(request, pegawai);
                    pegawai.setRefSkPegawaiId(riwayatSk.getId());
                    pegawai.setMkgTahun(0);
                    pegawai.setMkgBulan(0);
                    repository.save(pegawai);
                    break;
                default:
                    saveCapeg(request, pegawai);
                    break;
            }

            authService.createUser(pegawai);

            return SavedStatus.build(ESaveStatus.SUCCESS, "ok");

        } catch (Exception e) {
            log.error("pegawai: {}", e.getMessage());
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<PegawaiPostRequest> requests) {
        try {
            for (PegawaiPostRequest request : requests) {
                save(request);
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, "Success");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, PegawaiPutRequest request) {
        try {
            Optional<Pegawai> pegawai = repository.findById(id);
            if (pegawai.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pegawai");

            Biodata biodata = biodataRepository.findById(request.getNik()).orElseThrow(() -> new RuntimeException("Unknown Biodata"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId()).orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId()).orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Profesi profesi = request.getProfesiId() == null ? null :
                    profesiRepository.findById(request.getProfesiId()).orElse(null);
            Golongan golongan = request.getGolonganId() == null || ArrayUtils.contains(EXCLUDED_GOLONGAN_STATUSES, request.getStatusPegawai()) ||
                    ArrayUtils.contains(EXCLUDED_JABATAN_IDS, request.getJabatanId()) ? null :
                    golonganRepository.findById(request.getGolonganId())
                            .orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository.findById(request.getKodePajakId()).orElseThrow(() -> new RuntimeException("Unknown Kode Pajak"));

            Pegawai entity = PegawaiPutRequest.toEntity(pegawai.get(), request, biodata, jabatan, organisasi, profesi, golongan, kodePajak);
            Pegawai save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> patchGaji(Long id, PegawaiPatchGaji request) {
        try {
            Optional<Pegawai> pegawai = repository.findById(id);
            if (pegawai.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pegawai");

            GajiPendapatanNonPajak kodePajak = gajiPendapatanNonPajakRepository.findById(request.getKodePajakId()).orElseThrow(() -> new RuntimeException("Unknown Kode Pajak"));
            GajiProfil profilGaji = gajiProfilRepository.findById(request.getGajiProfilId()).orElseThrow(() -> new RuntimeException("Unknown Profil Gaji"));
            RumahDinas rumahDinas = rumahDinasRepository.findById(request.getRumahDinasId()).orElse(null);

            Pegawai entity = PegawaiPatchGaji.toEntity(pegawai.get(), request, kodePajak, profilGaji, rumahDinas);
            Pegawai save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> patchProfil(Long id, PegawaiPatchProfil request) {
        try {
            Optional<Pegawai> pegawai = repository.findById(id);
            if (pegawai.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pegawai");
            Golongan golongan = golonganRepository.findById(request.getId()).orElse(null);
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId()).orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId()).orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Profesi profesi = profesiRepository.findById(request.getProfesiId()).orElse(null);
            Pegawai entity = PegawaiPatchProfil.toEntity(pegawai.get(), request, golongan, organisasi, jabatan, profesi);
            Pegawai save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists) return false;
        repository.deleteById(id);
        return true;
    }

    private void saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk riwayatSk = riwayatSkService.saveCapeg(request, pegawai);
        pegawai.setRefSkCapegId(riwayatSk.getId());
        pegawai.setMkgTahun(0);
        pegawai.setMkgBulan(0);

        repository.save(pegawai);
    }
}
