package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HariLiburRepository extends JpaRepository<HariLibur, Long>,
RevisionRepository<HariLibur, Long, Integer> {
    Integer countByTanggalBetween(LocalDate startDate, LocalDate endDate);

    List<TanggalHariLibur> findByTanggalBetween(LocalDate first, LocalDate last);

    Optional<HariLibur> findByTanggalAndJenisLibur(LocalDate tanggal, EJenisLibur jenisLibur);
}
