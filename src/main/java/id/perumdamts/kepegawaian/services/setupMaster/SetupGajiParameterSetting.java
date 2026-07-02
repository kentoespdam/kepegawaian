package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiParameterSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGajiParameterSetting implements SetupMaster {
    private final GajiParameterSettingRepository repository;

    @Override
    public void insertBatch() {
        List<GajiParameterSetting> list = new ArrayList<>();
        list.add(new GajiParameterSetting("maksimal_potongan_jpn", 100_423D));
        list.add(new GajiParameterSetting("maksimal_potongan_askes", 120_000D));
        repository.saveAll(list);
    }
}
