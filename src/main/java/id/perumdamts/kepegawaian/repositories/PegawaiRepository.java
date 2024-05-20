package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>, JpaSpecificationExecutor<Pegawai> {
}
