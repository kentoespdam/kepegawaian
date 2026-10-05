package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DasarGajiRepository extends JpaRepository<DasarGaji, Long> {
    Optional<DasarGaji> findByDeskripsiAndTanggalAwal(String deskripsi, LocalDate tanggalAwal);
}
