package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PengalamanKerjaRepository extends JpaRepository<PengalamanKerja, Long>, JpaSpecificationExecutor<PengalamanKerja> {
}
