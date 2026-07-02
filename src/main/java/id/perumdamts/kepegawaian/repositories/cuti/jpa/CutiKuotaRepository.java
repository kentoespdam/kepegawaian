package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CutiKuotaRepository extends JpaRepository<CutiKuota, Long>,
        JpaSpecificationExecutor<CutiKuota>,
        RevisionRepository<CutiKuota, Long, Integer> {
    boolean existsByTahun(Integer tahun);

    Optional<CutiKuota> findByPegawai_IdAndTahun(Long pegawaiIdList, Integer tahun);

    <T> Optional<T> findRecordByPegawai_IdAndTahun(Long pegawaiIdList, Integer tahun, Class<T> type);

    <T> Optional<T> findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(Long pegawaiId, Integer tahun, LocalDate expired, Class<T> type);
}
