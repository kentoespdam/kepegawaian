package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiProfilRepository extends JpaRepository<GajiProfil, Long>,
        JpaSpecificationExecutor<GajiProfil>,
        RevisionRepository<GajiProfil, Long, Long> {
}
