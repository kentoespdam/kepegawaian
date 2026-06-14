package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;
import java.util.List;

public interface HariLiburRepository extends JpaRepository<HariLibur, Long>,
        JpaSpecificationExecutor<HariLibur>,
        RevisionRepository<HariLibur, Long, Integer> {
    Integer countByTanggalBetween(LocalDate startDate, LocalDate endDate);

    List<TanggalHariLibur> findByTanggalBetween(LocalDate first, LocalDate last);
}
