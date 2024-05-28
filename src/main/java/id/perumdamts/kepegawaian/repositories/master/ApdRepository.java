package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Apd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApdRepository extends JpaRepository<Apd, Long>, JpaSpecificationExecutor<Apd> {
    List<Apd> findByProfesi_Id(Long id);
}
