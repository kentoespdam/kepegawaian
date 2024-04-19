package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface KartuIdentitasRepository extends
        JpaRepository<KartuIdentitas, Long>,
        JpaSpecificationExecutor<KartuIdentitas>,
        QueryByExampleExecutor<KartuIdentitas> {
    List<KartuIdentitas> findByBiodata_Nik(String nik);
}
