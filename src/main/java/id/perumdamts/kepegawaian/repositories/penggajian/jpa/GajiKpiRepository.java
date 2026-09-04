package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiKpiRepository extends JpaRepository<GajiKpi, Long>,
        JpaSpecificationExecutor<GajiKpi>,
        RevisionRepository<GajiKpi, Long, Integer> {
    Optional<GajiKpi> findByNipamAndPeriode(String nipam, String periode);
}
