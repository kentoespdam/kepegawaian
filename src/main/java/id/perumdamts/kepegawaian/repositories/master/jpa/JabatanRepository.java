package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface JabatanRepository extends JpaRepository<Jabatan, Long>,
RevisionRepository<Jabatan, Long, Integer> {

    boolean existsByParentIdAndIsDeletedFalse(Long parentId);

    Optional<Jabatan> findByKode(String kode);
}
