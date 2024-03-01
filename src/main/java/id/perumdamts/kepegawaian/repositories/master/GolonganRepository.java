package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GolonganRepository extends JpaRepository<Golongan, Long>, JpaSpecificationExecutor<Golongan> {
    Optional<Golongan> findByNama(String nama);
}
