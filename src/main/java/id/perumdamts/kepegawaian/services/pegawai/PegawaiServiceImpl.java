package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.*;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PegawaiServiceImpl implements PegawaiService {
    private final PegawaiRepository repository;
    private final BiodataRepository biodataRepository;
    private final StatusPegawaiRepository statusPegawaiRepository;
    private final JabatanRepository jabatanRepository;
    private final OrganisasiRepository organisasiRepository;
    private final ProfesiRepository profesiRepository;
    private final GolonganRepository golonganRepository;
    private final GradeRepository gradeRepository;
    private final StatusKerjaRepository statusKerjaRepository;

    @Override
    public Page<PegawaiResponse> findPage(PegawaiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PegawaiResponse::from);
    }

    @Override
    public List<PegawaiResponse> findAll() {
        return repository.findAll().stream().map(PegawaiResponse::from).toList();
    }

    @Override
    public PegawaiResponse findById(Long id) {
        return repository.findById(id).map(PegawaiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PegawaiPostRequest request) {
        try {
            Biodata biodata = biodataRepository.findById(request.getNik())
                    .orElseThrow(() -> new RuntimeException("Unknown Biodata"));
            StatusPegawai statusPegawai = statusPegawaiRepository.findById(request.getStatusPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Status Pegawai"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Unknown Grade"));
            StatusKerja statusKerja = statusKerjaRepository.findById(request.getStatusKerjaId())
                    .orElseThrow(() -> new RuntimeException("Unknown Status Kerja"));

            Optional<Pegawai> pegawai = repository.findOne(request.getSpecification());
            if (pegawai.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Pegawai is Exists");

            Pegawai entity = PegawaiPostRequest.toEntity(
                    request,
                    biodata,
                    statusPegawai,
                    jabatan,
                    organisasi,
                    profesi,
                    golongan,
                    grade,
                    statusKerja
            );
            Pegawai save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
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
            if (pegawai.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pegawai");

            Biodata biodata = biodataRepository.findById(request.getNik())
                    .orElseThrow(() -> new RuntimeException("Unknown Biodata"));
            StatusPegawai statusPegawai = statusPegawaiRepository.findById(request.getStatusPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Status Pegawai"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Golongan golongan = golonganRepository.findById(request.getGolonganId())
                    .orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Unknown Grade"));
            StatusKerja statusKerja = statusKerjaRepository.findById(request.getStatusKerjaId())
                    .orElseThrow(() -> new RuntimeException("Unknown Status Kerja"));

            Pegawai entity = PegawaiPutRequest.toEntity(
                    pegawai.get(),
                    request,
                    biodata,
                    statusPegawai,
                    jabatan,
                    organisasi,
                    profesi,
                    golongan,
                    grade,
                    statusKerja
            );
            Pegawai save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
