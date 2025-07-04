package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CutiKuotaRepository extends JpaRepository<CutiKuota, Long>,
        JpaSpecificationExecutor<CutiKuota>,
        RevisionRepository<CutiKuota, Long, Long> {
    boolean existsByTahun(Integer tahun);

    List<CutiKuota> findByPegawai_IdInAndTahun(List<Long> pegawaiIdList, Integer tahun);

    Optional<CutiKuota> findByPegawai_IdAndTahun(Long pegawaiIdList, Integer tahun);

    <T> Optional<T> findRecordByPegawai_IdAndTahun(Long pegawaiIdList, Integer tahun, Class<T> type);

    Optional<CutiKuota> findOneByPegawai_IdAndTahunAndExpiredGreaterThan(Long pegawaiId, Integer tahun, LocalDate expired);
    <T> Optional<T> findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(Long pegawaiId, Integer tahun, LocalDate expired, Class<T> type);
}
