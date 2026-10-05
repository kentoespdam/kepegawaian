package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenjangPendidikanRepository extends JpaRepository<JenjangPendidikan, Long> {
    Optional<JenjangPendidikan> findByNama(String nama);
}
