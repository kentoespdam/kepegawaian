package id.perumdamts.kepegawaian.repositories.profil;

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
