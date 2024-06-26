package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatusPegawaiRepository extends JpaRepository<StatusPegawai, Long>, JpaSpecificationExecutor<StatusPegawai> {
}
