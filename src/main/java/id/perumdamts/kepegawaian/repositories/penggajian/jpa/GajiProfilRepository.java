package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiProfilRepository extends JpaRepository<GajiProfil, Long>,
RevisionRepository<GajiProfil, Long, Integer> {
    boolean existsByNamaIgnoreCase(String nama);
}
