package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;

public interface ProfesiRepository extends JpaRepository<Profesi, Long>,
        JpaSpecificationExecutor<Profesi>,
        RevisionRepository<Profesi, Long, Long> {
    List<Profesi> findByJabatan_Id(Long id);
}
