package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenjangPendidikanRepository extends JpaRepository<JenjangPendidikan, Long>, JpaSpecificationExecutor<JenjangPendidikan> {
}
