package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiParameterSettingRepository extends JpaRepository<GajiParameterSetting, Long>,
        JpaSpecificationExecutor<GajiParameterSetting>,
        RevisionRepository<GajiParameterSetting, Long, Integer> {
    Optional<GajiParameterSetting> findByKode(String kode);
}
