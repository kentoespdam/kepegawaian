package id.perumdamts.kepegawaian.repositories.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface RiwayatSpRepository extends JpaRepository<RiwayatSp, Long>,
        JpaSpecificationExecutor<RiwayatSp>,
        RevisionRepository<RiwayatSp, Long, Long> {
}