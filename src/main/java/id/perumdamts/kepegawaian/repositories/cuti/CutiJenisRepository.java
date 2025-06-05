package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface CutiJenisRepository extends JpaRepository<CutiJenis, Long>,
        JpaSpecificationExecutor<CutiJenis>,
        RevisionRepository<CutiJenis, Long, Long> {
}
