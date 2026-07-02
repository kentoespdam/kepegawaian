package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiKomponenRepository extends JpaRepository<GajiKomponen, Long>,
        JpaSpecificationExecutor<GajiKomponen>,
        RevisionRepository<GajiKomponen, Long, Integer> {
}
