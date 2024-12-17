package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.KodePajak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KodePajakRepository extends
        JpaRepository<KodePajak, Long>,
        JpaSpecificationExecutor<KodePajak> {
}
