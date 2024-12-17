package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface RiwayatTerminasiRepository extends JpaRepository<RiwayatTerminasi, Long>,
        JpaSpecificationExecutor<RiwayatTerminasi>,
        RevisionRepository<RiwayatTerminasi, Long, Long> {
}
