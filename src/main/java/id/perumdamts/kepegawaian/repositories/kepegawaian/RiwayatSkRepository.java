package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RiwayatSkRepository extends JpaRepository<RiwayatSk, Long>, JpaSpecificationExecutor<RiwayatSk> {
}
