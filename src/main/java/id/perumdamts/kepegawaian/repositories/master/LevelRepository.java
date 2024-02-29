package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long>, JpaSpecificationExecutor<Level> {
    Optional<Level> findByNama(String nama);
}
