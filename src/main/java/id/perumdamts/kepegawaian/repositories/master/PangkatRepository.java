package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Pangkat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PangkatRepository extends JpaRepository<Pangkat, Long>, JpaSpecificationExecutor<Pangkat> {
    Optional<Pangkat> findByNama(String nama);
}
