package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JenisKeahlianRepository extends JpaRepository<JenisKeahlian, Long>, JpaSpecificationExecutor<JenisKeahlian> {

}
