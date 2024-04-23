package id.perumdamts.kepegawaian.repositories.profil;

import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PengalamanKerjaRepository extends JpaRepository<PengalamanKerja, Long>, JpaSpecificationExecutor<PengalamanKerja> {
    List<PengalamanKerja> findByBiodata_Nik(String biodataId);
}
