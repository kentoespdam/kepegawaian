package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface KartuIdentitasRepository extends
        JpaRepository<KartuIdentitas, Long>,
        JpaSpecificationExecutor<KartuIdentitas>,
        RevisionRepository<KartuIdentitas, Long, Integer>,
        QueryByExampleExecutor<KartuIdentitas> {
}
