package id.perumdamts.kepegawaian.services.master.apd;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPutRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdResponse;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.master.ApdRepository;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApdServiceImpl implements ApdService {
    private final ApdRepository repository;
    private final ProfesiRepository profesiRepository;

    @Override
    public List<ApdResponse> findAll() {
        return repository.findAll().stream().map(ApdResponse::from).toList();
    }

    @Override
    public Page<ApdResponse> findPage(ApdRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ApdResponse::from);
    }

    @Override
    public ApdResponse findById(Long id) {
        return repository.findById(id).map(ApdResponse::from).orElse(null);
    }

    @Override
    public List<ApdResponse> findByProfesiId(Long id) {
        return repository.findByProfesi_Id(id).stream().map(ApdResponse::from).toList();
    }

    @Override
    public SavedStatus<?> save(ApdPostRequest request) {
        try {
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Apd sudah ada");
            Apd entity = ApdPostRequest.toEntity(request, profesi);
            Apd save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> saveBatch(List<ApdPostRequest> requests) {
        return null;
    }

    @Override
    public SavedStatus<?> update(Long id, ApdPutRequest request) {
        try {
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Optional<Apd> apd = repository.findById(id);
            if (apd.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Apd");
            Apd entity = ApdPutRequest.toEntity(apd.get(), request, profesi);
            Apd save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<Apd> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
