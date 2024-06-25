package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProfesiRepository extends JpaRepository<Profesi, Long>, JpaSpecificationExecutor<Profesi> {
    List<Profesi> findByLevel_Id(Long id);
}
