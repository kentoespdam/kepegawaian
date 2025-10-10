package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface ProfilKeluargaRepository extends
        JpaRepository<ProfilKeluarga, Long>,
        JpaSpecificationExecutor<ProfilKeluarga>,
        RevisionRepository<ProfilKeluarga, Long, Integer> {

    Optional<ProfilKeluarga> findByIdAndChangedStatus(Long id, Boolean changedStatus);
}
