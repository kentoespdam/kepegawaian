package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.repositories.penggajian.DasarGajiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DasarGajiServiceImpl implements DasarGajiService {
    private final DasarGajiRepository repository;

    @Override
    public List<DasarGajiResponse> findAll(DasarGajiRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(DasarGajiResponse::from).toList();
    }

    @Override
    public Page<DasarGajiResponse> findPage(DasarGajiRequest request) {
        return  repository.findAll(request.getSpecification(), request.getPageable())
                .map(DasarGajiResponse::from);
    }

    @Override
    public DasarGajiResponse findById(Long id) {
        return repository.findById(id).map(DasarGajiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(DasarGajiPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Dasar Gaji sudah ada");
        DasarGaji entity = DasarGajiPostRequest.toEntity(request);
        DasarGaji save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<DasarGajiPostRequest> requests) {
        try{
            requests.stream().map(DasarGajiPostRequest::toEntity).forEach(repository::save);
            return SavedStatus.build(ESaveStatus.SUCCESS, null);
        }catch (Exception e){
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, DasarGajiPutRequest request) {
        Optional<DasarGaji> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Dasar Gaji not found");
        DasarGaji entity = DasarGajiPutRequest.toEntity(byId.get(), request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
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
