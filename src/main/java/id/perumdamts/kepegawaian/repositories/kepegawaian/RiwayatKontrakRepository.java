package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface RiwayatKontrakRepository extends JpaRepository<RiwayatKontrak, Long>,
        JpaSpecificationExecutor<RiwayatKontrak>,
        RevisionRepository<RiwayatKontrak, Long, Integer> {
}
