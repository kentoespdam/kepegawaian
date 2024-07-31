package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenisMutasiRepository extends JpaRepository<JenisMutasi, Long>, JpaSpecificationExecutor<JenisMutasi> {
}
