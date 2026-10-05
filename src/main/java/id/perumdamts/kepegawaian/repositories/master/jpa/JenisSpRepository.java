package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface JenisSpRepository extends JpaRepository<JenisSp, Long>,
RevisionRepository<JenisSp, Long, Integer> {
    Optional<JenisSp> findByKode(String kode);
}
