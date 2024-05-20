package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatusKerjaRepository extends JpaRepository<StatusKerja, Long>, JpaSpecificationExecutor<StatusKerja> {

}
