package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GajiKpiRepository extends JpaRepository<GajiKpi, Long> {
    Optional<GajiKpi> findByNipamAndPeriode(String nipam, String periode);

    List<GajiKpi> findByPeriode(String periode);

    List<GajiKpi> findByPeriodeAndNipamIn(String periode, Collection<String> nipams);
}
