package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPutRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiResponse;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.repositories.master.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfesiServiceImpl implements ProfesiService {
    private final ProfesiRepository repository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final GradeRepository gradeRepository;

    @Override
    public List<ProfesiResponse> findAll() {
        return repository.findAll().stream().map(ProfesiResponse::from).toList();
    }

    @Override
    public List<ProfesiResponse> findByLevel(Long id) {
        return repository.findByLevel_Id(id).stream().map(ProfesiResponse::from).toList();
    }

    @Override
    public Page<ProfesiResponse> findPage(ProfesiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ProfesiResponse::from);
    }

    @Override
    public ProfesiResponse findById(Long id) {
        return repository.findById(id).map(ProfesiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(ProfesiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Profesi sudah ada");
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Unknown Grade"));

            Profesi entity = ProfesiPostRequest.toEntity(request, organisasi, jabatan, grade);
            Profesi save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, ProfesiPutRequest request) {
        try {
            Optional<Profesi> byId = repository.findById(id);
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Profesi");

            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Unknown Grade"));

            Profesi entity = ProfesiPutRequest.toEntity(byId.get(), request, organisasi, jabatan, grade);
            Profesi save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Profesi> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
