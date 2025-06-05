package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburResponse;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HariLiburServiceImpl implements HariLiburService {
    private final HariLiburRepository repository;

    @Override
    public Page<HariLiburResponse> findPage(HariLiburRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(HariLiburResponse::from);
    }

    @Override
    public HariLiburResponse findById(Long id) {
        return repository.findById(id).map(HariLiburResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(HariLiburPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Hari Libur sudah ada");
            HariLibur entity = HariLiburPostRequest.toEntity(request);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Saved!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, HariLiburPutRequest request) {
        Optional<HariLibur> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Data Not Found!");
        HariLibur entity = HariLiburPutRequest.toEntity(byId.get(), request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Data Updated!");
    }

    @Override
    public Boolean delete(Long id) {
        Optional<HariLibur> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
