package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenisPelatihanRepository extends JpaRepository<JenisPelatihan, Long>, JpaSpecificationExecutor<JenisPelatihan> {
}
