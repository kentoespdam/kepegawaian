package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CutiKlaimDetailRepository extends JpaRepository<CutiKlaimDetail, Long> {
    List<CutiKlaimDetail> findByRefCuti_id(Long id);
}
