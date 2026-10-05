package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenisKitasRepository extends JpaRepository<JenisKitas, Long> {
    Optional<JenisKitas> findByNama(String nama);
}
