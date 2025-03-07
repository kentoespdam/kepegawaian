package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DasarGajiRepository extends JpaRepository<DasarGaji, Long>, JpaSpecificationExecutor<DasarGaji> {
}
