package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiPhdpRepository extends JpaRepository<GajiPhdp, Long>,
        JpaSpecificationExecutor<GajiPhdp>,
        RevisionRepository<GajiPhdp, Long, Long> {
}
