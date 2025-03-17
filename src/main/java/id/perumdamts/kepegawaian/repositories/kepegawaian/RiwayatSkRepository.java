package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RiwayatSkRepository extends JpaRepository<RiwayatSk, Long>, JpaSpecificationExecutor<RiwayatSk> {
    List<RiwayatSk> findByIdIn(List<Long> riwayatIds);

    List<RiwayatSk> findByPegawai_Id(Long pegawaiId);
}
