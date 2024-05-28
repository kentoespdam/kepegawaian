package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrganisasiRepository extends JpaRepository<Organisasi, Long>, JpaSpecificationExecutor<Organisasi> {
    Optional<Organisasi> findByOrganisasi_Id(Long id);
}
