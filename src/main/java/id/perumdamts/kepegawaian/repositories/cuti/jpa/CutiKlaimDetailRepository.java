package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;

public interface CutiKlaimDetailRepository extends JpaRepository<CutiKlaimDetail, Long> {
    List<CutiKlaimDetail> findByRefCuti_id(Long id);
}
