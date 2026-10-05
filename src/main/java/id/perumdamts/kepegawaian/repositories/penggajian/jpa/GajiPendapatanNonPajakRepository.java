package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiPendapatanNonPajakRepository extends JpaRepository<GajiPendapatanNonPajak, Long>,
RevisionRepository<GajiPendapatanNonPajak, Long, Integer> {
    boolean existsByKode(String kode);
}
