package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PendidikanRepository extends JpaRepository<Pendidikan, Long>, JpaSpecificationExecutor<Pendidikan> {
    @Modifying
    @Query("UPDATE Pendidikan p SET p.isLatest = FALSE WHERE p.id != :id AND p.biodata.nik = :nik")
    void updateIsLatest(@Param("id") Long id, @Param("nik") String nik);

    @Transactional
    @Modifying
    @Query(value = """
            update pendidikan p set
                    p.biodata_id = ?1, p.jenjang_id = ?2, p.gelar_depan = ?3, p.gelar_belakang = ?4, p.jurusan = ?5, p.institusi = ?6,
                    p.kota = ?7, p.tahun_masuk = ?8, p.is_lulus = ?9, p.tahun_lulus = ?10, p.gpa = ?11, p.is_latest = ?12, p.changed_status = ?13
            where p.id = ?14
            """, nativeQuery = true)
    void rollbackPrevVersion(
            String nik,
            Long jenjangId,
            String gelarDepan,
            String gelarBelakang,
            String jurusan,
            String institusi,
            String kota,
            Integer tahunMasuk,
            Boolean isLulus,
            Integer tahunLulus,
            Double gpa,
            Boolean isLatest,
            Boolean changedStatus,
            Long id
    );

}
