package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RiwayatMutasiRepository extends JpaRepository<RiwayatMutasi, Long>, JpaSpecificationExecutor<RiwayatMutasi> {
}
