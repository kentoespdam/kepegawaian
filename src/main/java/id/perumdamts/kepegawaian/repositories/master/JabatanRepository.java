package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JabatanRepository extends JpaRepository<Jabatan, Long>, JpaSpecificationExecutor<Jabatan> {
    List<Jabatan> findByOrganisasi_Id(Long id);
}
