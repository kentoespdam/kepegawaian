package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LampiranProfilRepository extends JpaRepository<LampiranProfil, Long>, JpaSpecificationExecutor<LampiranProfil> {
}
