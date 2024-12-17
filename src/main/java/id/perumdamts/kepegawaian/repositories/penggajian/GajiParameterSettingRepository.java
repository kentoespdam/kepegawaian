package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiParameterSettingRepository extends JpaRepository<GajiParameterSetting, Long>,
        JpaSpecificationExecutor<GajiParameterSetting>,
        RevisionRepository<GajiParameterSetting, Long, Long> {
}
