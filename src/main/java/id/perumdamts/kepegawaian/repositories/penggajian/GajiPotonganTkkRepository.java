package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiPotonganTkkRepository extends JpaRepository<GajiPotonganTkk, Long>,
        JpaSpecificationExecutor<GajiPotonganTkk>,
        RevisionRepository<GajiPotonganTkk, Long, Integer> {
}
