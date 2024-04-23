package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface KeahlianRepository extends JpaRepository<Keahlian, Long>, JpaSpecificationExecutor<Keahlian> {
    List<Keahlian> findByBiodata_Nik(String biodataId);
}
