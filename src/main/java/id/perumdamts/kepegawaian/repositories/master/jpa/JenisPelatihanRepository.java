package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenisPelatihanRepository extends JpaRepository<JenisPelatihan, Long> {
    Optional<JenisPelatihan> findByNama(String nama);
}
