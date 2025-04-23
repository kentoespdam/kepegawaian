package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface JenisSpRepository extends JpaRepository<JenisSp, Long>,
        JpaSpecificationExecutor<JenisSp>,
        RevisionRepository<JenisSp, Long, Long> {
}
