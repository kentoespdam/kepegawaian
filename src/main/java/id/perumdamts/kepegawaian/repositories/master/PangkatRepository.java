package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Pangkat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PangkatRepository extends JpaRepository<Pangkat, Long>, JpaSpecificationExecutor<Pangkat> {
}
