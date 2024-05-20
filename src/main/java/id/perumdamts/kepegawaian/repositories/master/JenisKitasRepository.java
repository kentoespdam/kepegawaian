package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenisKitasRepository extends JpaRepository<JenisKitas, Long>, JpaSpecificationExecutor<JenisKitas> {
}
