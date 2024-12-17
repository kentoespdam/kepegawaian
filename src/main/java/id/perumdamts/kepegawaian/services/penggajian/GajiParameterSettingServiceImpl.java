package id.perumdamts.kepegawaian.services.penggajian;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiParameterSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiParameterSettingServiceImpl implements GajiParameterSettingService {
    private final GajiParameterSettingRepository repository;

    @Override
    public Page<GajiParameterSettingResponse> findPage(GajiParameterSettingRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiParameterSettingResponse::from);
    }

    @Override
    public List<GajiParameterSettingResponse> findAll() {
        return repository.findAll().stream()
                .map(GajiParameterSettingResponse::from)
                .toList();
    }

    @Override
    public GajiParameterSettingResponse findById(Long id) {
        return repository.findById(id)
                .map(GajiParameterSettingResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(GajiParameterSettingPostRequest request) {
        Optional<GajiParameterSetting> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pendapatan Non Pajak sudah ada");
        GajiParameterSetting entity = GajiParameterSettingPostRequest.toEntity(request);
        GajiParameterSetting save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, GajiParameterSettingPutRequest request) {
        Optional<GajiParameterSetting> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pendapatan Non Pajak");
        GajiParameterSetting entity = GajiParameterSettingPutRequest.toEntity(byId.get(), request);
        GajiParameterSetting save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<GajiParameterSetting> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
