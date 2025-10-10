package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface KeahlianRepository extends JpaRepository<Keahlian, Long>,
        JpaSpecificationExecutor<Keahlian>,
        RevisionRepository<Keahlian, Long, Integer> {
}
