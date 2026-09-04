package id.perumdamts.kepegawaian.repositories.kepegawaian.jpa;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface RiwayatSpRepository extends JpaRepository<RiwayatSp, Long>,
        JpaSpecificationExecutor<RiwayatSp>,
        RevisionRepository<RiwayatSp, Long, Integer> {
    /**
     * Ada RiwayatSp SP-3 yang aktif pada periode gaji {@code periode} (format YYYY-MM):
     * interval SP [tanggalMulai, tanggalSelesai] overlap window gaji
     * [prev-month-21, current-month-20] — pola legacy emp_notice.notice_start_date.
     */
    @Query("""
            select case when count(r) > 0 then true else false end
            from RiwayatSp r
            where r.pegawai.id = ?1
              and r.jenisSp.kode = 'SP-3'
              and r.tanggalMulai <= ?2
              and (r.tanggalSelesai is null or r.tanggalSelesai >= ?3)
            """)
    boolean existsSp3Aktif(Long pegawaiId, LocalDate windowEnd, LocalDate windowStart);

    /**
     * Bulk query ID pegawai yang memiliki SP-3 aktif pada rentang window gaji.
     */
    @Query("""
            select distinct r.pegawai.id
            from RiwayatSp r
            where r.pegawai.id in ?1
              and r.jenisSp.kode = 'SP-3'
              and r.tanggalMulai <= ?2
              and (r.tanggalSelesai is null or r.tanggalSelesai >= ?3)
            """)
    Set<Long> findAllPegawaiIdsWithActiveSp3In(Collection<Long> pegawaiIds, LocalDate windowEnd, LocalDate windowStart);

    default Set<Long> findPegawaiIdsWithActiveSp3In(Collection<Long> pegawaiIds, LocalDate windowEnd, LocalDate windowStart) {
        return findAllPegawaiIdsWithActiveSp3In(pegawaiIds, windowEnd, windowStart);
    }
}
