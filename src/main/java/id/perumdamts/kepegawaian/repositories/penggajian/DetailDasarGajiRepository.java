package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DetailDasarGajiRepository extends JpaRepository<DetailDasarGaji, Long>, JpaSpecificationExecutor<DetailDasarGaji> {
    Optional<DetailDasarGaji> findDetailDasarGajiByGolongan_IdAndMkg(Long golonganId, Integer masaKerja);
}
