package id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiParameterSetting.GajiParameterSettingMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiParameterSettingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiParameterSettingCommandService {
    private final GajiParameterSettingRepository repository;

    @Transactional
    public SavedStatus<Long> save(GajiParameterSettingPostRequest request) {
        Optional<GajiParameterSetting> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            throw new ConflictException("Setting Parameter Gaji sudah ada");
        GajiParameterSetting entity = GajiParameterSettingMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiParameterSettingPutRequest request) {
        GajiParameterSetting entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Setting Parameter Gaji not found"));
        GajiParameterSettingMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiParameterSetting> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
