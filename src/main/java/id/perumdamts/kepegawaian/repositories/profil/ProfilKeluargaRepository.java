package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProfilKeluargaRepository extends
        JpaRepository<ProfilKeluarga, Long>,
        JpaSpecificationExecutor<ProfilKeluarga> {
    List<ProfilKeluarga> findByBiodata_Nik(String biodataId);
}
