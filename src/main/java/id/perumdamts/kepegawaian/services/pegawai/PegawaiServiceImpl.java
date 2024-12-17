package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.*;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.GenericKontrakService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PegawaiServiceImpl implements PegawaiService {
    private final PegawaiRepository repository;
    private final BiodataRepository biodataRepository;
    private final JabatanRepository jabatanRepository;
    private final OrganisasiRepository organisasiRepository;
    private final ProfesiRepository profesiRepository;
    private final GolonganRepository golonganRepository;
    private final GradeRepository gradeRepository;
    private final BiodataService biodataService;
    private final RiwayatSkService riwayatSkService;
    private final GenericKontrakService genericKontrakService;
    private final GajiPendapatanNonPajakRepository gajiPendapatanNonPajakRepository;
    private final GajiProfilRepository gajiProfilRepository;
    private final RumahDinasRepository rumahDinasRepository;

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
            List<Long> riwayatIds = new ArrayList<>();
            if (Objects.nonNull(pegawai.getRefSkCapegId())) riwayatIds.add(pegawai.getRefSkCapegId());
            if (Objects.nonNull(pegawai.getRefSkPegawaiId())) riwayatIds.add(pegawai.getRefSkPegawaiId());
            if (Objects.nonNull(pegawai.getRefSkGolId())) riwayatIds.add(pegawai.getRefSkGolId());
            if (Objects.nonNull(pegawai.getRefSkJabatanId())) riwayatIds.add(pegawai.getRefSkJabatanId());
            if (Objects.nonNull(pegawai.getRefSkMutasiId())) riwayatIds.add(pegawai.getRefSkMutasiId());
            List<RiwayatSkResponse> riwayatSkResponses = riwayatSkService.findByIds(riwayatIds);
            return PegawaiResponseDetail.from(pegawai, riwayatSkResponses);
        }).orElse(null);
    }

    @Override
    public PegawaiResponseRingkasan findRingkasan(Long id) {
        return repository.findById(id)
                .map(PegawaiResponseRingkasan::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PegawaiPostRequest request) {
        try {
            Biodata biodata = biodataService.saveFromPegawai(request);
            if (request.getStatusPegawai().equals(EStatusPegawai.NON_PEGAWAI)) {
                return SavedStatus.build(ESaveStatus.SUCCESS, biodata);
            }

            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId()).orElseThrow();
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId()).orElseThrow();
            Profesi profesi = profesiRepository.findById(request.getProfesiId()).orElseThrow();
            Golongan golongan = request.getStatusPegawai().equals(EStatusPegawai.KONTRAK) ? null
                    : golonganRepository.findById(request.getGolonganId()).orElseThrow();
            Grade grade = gradeRepository.findById(request.getGradeId()).orElseThrow();

            Pegawai entity = PegawaiPostRequest.toEntity(request, biodata, jabatan, organisasi, profesi, golongan, grade);
            Pegawai pegawai = repository.save(entity);

            switch (request.getStatusPegawai()) {
                case KONTRAK:
                    genericKontrakService.saveFromPegawai(request, pegawai);
                    break;
                case PEGAWAI:
                    RiwayatSk riwayatSk = riwayatSkService.savePegawai(request, pegawai);
                    pegawai.setRefSkPegawaiId(riwayatSk.getId());
                    pegawai.setMkgTahun(0);
                    pegawai.setMkgBulan(0);

                    pegawai = repository.save(pegawai);
                    break;
                default:
                    pegawai = saveCapeg(request, pegawai);
                    break;
            }

            return SavedStatus.build(ESaveStatus.SUCCESS, pegawai);

        } catch (Exception e) {
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
            Profesi profesi = profesiRepository.findById(request.getProfesiId()).orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            Grade grade = gradeRepository.findById(request.getGradeId()).orElseThrow(() -> new RuntimeException("Unknown Grade"));

            Pegawai entity = PegawaiPutRequest.toEntity(pegawai.get(), request, biodata, jabatan, organisasi, profesi, golongan, grade);
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
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists) return false;
        repository.deleteById(id);
        return true;
    }

    private Pegawai saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk riwayatSk = riwayatSkService.saveCapeg(request, pegawai);
        pegawai.setRefSkCapegId(riwayatSk.getId());
        pegawai.setMkgTahun(0);
        pegawai.setMkgBulan(0);

        return repository.save(pegawai);
    }
}
