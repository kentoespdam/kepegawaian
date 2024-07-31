package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisSk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenisSkRepository extends JpaRepository<JenisSk, Long>, JpaSpecificationExecutor<JenisSk> {
}
