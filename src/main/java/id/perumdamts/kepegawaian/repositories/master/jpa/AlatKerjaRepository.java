package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlatKerjaRepository extends JpaRepository<AlatKerja, Long> {
    boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId);
}
