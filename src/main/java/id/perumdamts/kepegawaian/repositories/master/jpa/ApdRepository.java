package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Apd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApdRepository extends JpaRepository<Apd, Long> {
    boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId);
}
