package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface ProfilKeluargaRepository extends
        JpaRepository<ProfilKeluarga, Long>,
        JpaSpecificationExecutor<ProfilKeluarga>,
        RevisionRepository<ProfilKeluarga, Long, Integer> {

    /**
     * Finds an active (is_deleted=false) ProfilKeluarga row by biodataId + nama + tanggalLahir.
     * Bypasses {@code @SQLRestriction} so both active and archived rows are visible to the caller;
     * we filter by is_deleted=false explicitly to detect the arsip & aktif berdampingan duplicate.
     */
    @Query("""
            select p from ProfilKeluarga p
            where p.biodata.nik = :biodataId
              and p.nama = :nama
              and p.tanggalLahir = :tanggalLahir
              and p.isDeleted = false
            """)
    java.util.Optional<ProfilKeluarga> findActiveByBiodataIdAndNamaAndTanggalLahir(
            String biodataId, String nama, LocalDate tanggalLahir);

    @Transactional
    @Modifying
    @Query(
            value = """
                    update profil_keluarga p set
                        p.nik = ?1, p.nama = ?2, p.jenis_kelamin = ?3, p.agama = ?4, p.hubungan_keluarga = ?5,
                        p.tempat_lahir = ?6, p.tanggal_lahir = ?7, p.tanggungan = ?8, p.pendidikan_id = ?9,
                        p.status_pendidikan = ?10, p.status_kawin = ?11, p.notes = ?12, p.biodata_id = ?13,
                        p.changed_status=?14
                    where p.id = ?15""",
            nativeQuery = true
    )
    void rollbackPrevVersion(
            String nik,
            String nama,
            Integer jenisKelamin,
            Integer agama,
            Integer hubunganKeluarga,
            String tempatLahir,
            LocalDate tanggalLahir,
            Boolean tanggungan,
            @Nullable Long pendidikanId,
            Integer statusPendidikan,
            Boolean statusKawin,
            String notes,
            String biodataId,
            Boolean changeStatus,
            Long id
    );
}
