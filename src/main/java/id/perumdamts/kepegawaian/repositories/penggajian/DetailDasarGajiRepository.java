package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DetailDasarGajiRepository extends JpaRepository<DetailDasarGaji, Long>, JpaSpecificationExecutor<DetailDasarGaji> {
}
