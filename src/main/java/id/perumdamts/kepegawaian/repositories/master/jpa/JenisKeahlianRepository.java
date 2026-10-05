package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenisKeahlianRepository extends JpaRepository<JenisKeahlian, Long> {
    Optional<JenisKeahlian> findByNama(String nama);
}
