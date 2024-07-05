package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PendidikanRepository extends JpaRepository<Pendidikan, Long>, JpaSpecificationExecutor<Pendidikan> {
    Page<Pendidikan> findByBiodata_Nik(String biodataId, Pageable pageable);

    @Modifying
    @Query( "UPDATE Pendidikan p SET p.disetujui = FALSE WHERE p.biodata.nik = :nik")
    void updateByBiodata_Nik(@Param("nik") String nik);
}
