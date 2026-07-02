package id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiParameterSettingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiParameterSettingQueryService {
    private final GajiParameterSettingQueryRepository queryRepository;

    public Page<GajiParameterSettingResponse> findPage(GajiParameterSettingIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiParameterSettingResponse> findAll(GajiParameterSettingIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiParameterSettingResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
