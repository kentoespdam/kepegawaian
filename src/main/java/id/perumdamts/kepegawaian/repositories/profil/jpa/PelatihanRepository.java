package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PelatihanRepository extends JpaRepository<Pelatihan, Long>, JpaSpecificationExecutor<Pelatihan> {
}
