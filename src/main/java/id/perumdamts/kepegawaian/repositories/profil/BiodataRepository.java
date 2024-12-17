package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface BiodataRepository extends
        JpaRepository<Biodata, String>,
        JpaSpecificationExecutor<Biodata>,
        QueryByExampleExecutor<Biodata>,
        RevisionRepository<Biodata, String, Long> {
}
