package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>, JpaSpecificationExecutor<Pegawai> {
    Optional<Pegawai> findByBiodata_Nik(String nik);
}
