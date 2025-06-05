package id.perumdamts.kepegawaian.services.cuti.jenis;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CutiJenisServiceImpl implements CutiJenisService {
    private final CutiJenisRepository repository;

    @Override
    public Page<CutiJenisResponse> findPage(CutiJenisRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiJenisResponse::from);
    }

    @Override
    public List<CutiJenisResponse> findList(CutiJenisRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(CutiJenisResponse::from).toList();
    }

    @Override
    public CutiJenisResponse findById(Long id) {
        return repository.findById(id).map(CutiJenisResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(CutiJenisPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Cuti Jenis sudah ada");
            CutiJenis parent = repository.findById(request.getParentId()).orElse(null);
            CutiJenis entity = CutiJenisPostRequest.toEntity(request, parent);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Saved!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, CutiJenisPutRequest request) {
        try {
            Optional<CutiJenis> byId = repository.findById(id);
            if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Data Not Found!");
            CutiJenis parent = repository.findById(request.getParentId()).orElse(null);
            CutiJenis entity = CutiJenisPutRequest.toEntity(byId.get(), request, parent);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Updated!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(Long id) {
        Optional<CutiJenis> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
