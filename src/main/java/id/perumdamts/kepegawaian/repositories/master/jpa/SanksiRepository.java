package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Sanksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface SanksiRepository extends JpaRepository<Sanksi, Long>,
RevisionRepository<Sanksi, Long, Integer> {

    boolean existsByJenisSpIdAndIsDeletedFalse(Long jenisSpId);

    Optional<Sanksi> findByKode(String kode);
}
