package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CutiKlaimDetailRepository extends JpaRepository<CutiKlaimDetail, Long> {
    void deleteByCutiPegawai_Id(Long id);
}
