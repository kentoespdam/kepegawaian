package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface JabatanRepository extends JpaRepository<Jabatan, Long>,
        JpaSpecificationExecutor<Jabatan>,
        RevisionRepository<Jabatan, Long, Integer> {

    boolean existsByParentIdAndIsDeletedFalse(Long parentId);
}
