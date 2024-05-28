package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlatKerjaRepository extends JpaRepository<AlatKerja, Long>, JpaSpecificationExecutor<AlatKerja> {
    List<AlatKerja> findByProfesi_Id(Long id);
}
