package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LampiranProfilRepository extends JpaRepository<LampiranProfil, Long>, JpaSpecificationExecutor<LampiranProfil> {
    List<LampiranProfil> findByRefAndRefId(EJenisLampiranProfil eJenisLampiranProfil, Long id);
}
