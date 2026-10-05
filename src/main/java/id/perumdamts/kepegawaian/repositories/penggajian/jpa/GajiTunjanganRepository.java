package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiTunjanganRepository extends JpaRepository<GajiTunjangan, Long>,
RevisionRepository<GajiTunjangan, Long, Integer> {
    Optional<GajiTunjangan> findByIdAndJenisTunjangan(Long id, EJenisTunjangan eJenisTunjangan);

    Optional<GajiTunjangan> findByJenisTunjanganAndLevelIdAndGolonganIsNull(EJenisTunjangan jenis, Long levelId);

    Optional<GajiTunjangan> findByJenisTunjanganAndGolonganId(EJenisTunjangan jenis, Long golonganId);

    boolean existsByJenisTunjanganAndLevelIdAndGolonganId(EJenisTunjangan jenis, Long levelId, Long golonganId);

    boolean existsByJenisTunjanganAndLevelIdAndGolonganIsNull(EJenisTunjangan jenis, Long levelId);

    boolean existsByJenisTunjanganAndGolonganId(EJenisTunjangan jenis, Long golonganId);
}
