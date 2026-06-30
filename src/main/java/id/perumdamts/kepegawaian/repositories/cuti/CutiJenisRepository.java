package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CutiJenisRepository extends JpaRepository<CutiJenis, Long>,
        JpaSpecificationExecutor<CutiJenis>,
        RevisionRepository<CutiJenis, Long, Integer> {

    @Query(value = "SELECT * FROM cuti_jenis WHERE LOWER(nama) = LOWER(:nama) AND is_deleted = true LIMIT 1", nativeQuery = true)
    Optional<CutiJenis> findDeletedByName(@Param("nama") String nama);
}
