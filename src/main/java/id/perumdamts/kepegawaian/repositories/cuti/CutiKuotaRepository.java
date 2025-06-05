package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;

public interface CutiKuotaRepository extends JpaRepository<CutiKuota, Long>,
        JpaSpecificationExecutor<CutiKuota>,
        RevisionRepository<CutiKuota, Long, Long> {
    List<CutiKuota> findByPegawai_Id(Long pegawaiId);

    boolean findExistByTahun(Integer tahun);
}
