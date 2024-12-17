package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiPendapatanNonPajakRepository extends JpaRepository<GajiPendapatanNonPajak, Long>,
        JpaSpecificationExecutor<GajiPendapatanNonPajak>,
        RevisionRepository<GajiPendapatanNonPajak, Long, Long> {
}
