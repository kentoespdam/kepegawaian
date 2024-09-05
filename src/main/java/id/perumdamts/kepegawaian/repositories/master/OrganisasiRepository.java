package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface OrganisasiRepository extends JpaRepository<Organisasi, Long>,
        JpaSpecificationExecutor<Organisasi>,
        RevisionRepository<Organisasi, Long, Long> {
}
