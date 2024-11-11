package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiTunjanganRepository extends JpaRepository<GajiTunjangan, Long>,
        JpaSpecificationExecutor<GajiTunjangan>,
        RevisionRepository<GajiTunjangan, Long, Long> {
    Optional<GajiTunjangan> findByIdAndJenisTunjangan(Long id, EJenisTunjangan eJenisTunjangan);

    boolean existsByIdAndJenisTunjangan(Long id, EJenisTunjangan jenis);
}
