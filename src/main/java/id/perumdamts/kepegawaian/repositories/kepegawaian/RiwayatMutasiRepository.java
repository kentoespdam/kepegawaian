package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface RiwayatMutasiRepository extends JpaRepository<RiwayatMutasi, Long>,
        JpaSpecificationExecutor<RiwayatMutasi>,
        RevisionRepository<RiwayatMutasi, Long, Long> {
}
