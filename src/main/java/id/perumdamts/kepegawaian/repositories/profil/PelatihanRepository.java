package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PelatihanRepository extends JpaRepository<Pelatihan, Long>, JpaSpecificationExecutor<Pelatihan> {
    List<Pelatihan> findByBiodata_Nik(String biodataId);
}
