package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrganisasiRepository extends JpaRepository<Organisasi, Long>,
        JpaSpecificationExecutor<Organisasi> {
}
