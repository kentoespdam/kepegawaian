package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.HariLibur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;

public interface HariLiburRepository extends JpaRepository<HariLibur, Long>,
        JpaSpecificationExecutor<HariLibur>,
        RevisionRepository<HariLibur, Long, Long> {
    Integer countByTanggalBetween(LocalDate startDate, LocalDate endDate);
}
