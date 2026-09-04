package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;

public interface GajiKomponenRepository extends JpaRepository<GajiKomponen, Long>,
        JpaSpecificationExecutor<GajiKomponen>,
        RevisionRepository<GajiKomponen, Long, Integer> {
    List<GajiKomponen> findByProfilGajiIdOrderByUrutAsc(Long profilGajiId);

    List<GajiKomponen> findByOrderByUrutAsc();
}
