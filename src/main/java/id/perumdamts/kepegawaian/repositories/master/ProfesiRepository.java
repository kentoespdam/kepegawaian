package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import org.jooq.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfesiRepository extends JpaRepository<Profesi, Log>, JpaSpecificationExecutor<Profesi> {
}
