package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiParameterSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupGajiParameterSetting implements SetupMaster {
    @Autowired
    private GajiParameterSettingRepository repository;
    @Override
    public void insertBatch() throws JsonProcessingException {
        List<GajiParameterSetting> list=new ArrayList<>();
        list.add(new GajiParameterSetting(1L, "maksimal_potongan_jpn",100_423D));
        list.add(new GajiParameterSetting(2L, "maksimal_potongan_askes",120_000D));
        repository.saveAll(list);
    }
}
