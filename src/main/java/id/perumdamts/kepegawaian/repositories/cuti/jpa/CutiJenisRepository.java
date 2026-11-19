package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;

public interface CutiJenisRepository extends JpaRepository<CutiJenis, Long>,
        RevisionRepository<CutiJenis, Long, Integer> {

    boolean existsByNamaIgnoreCase(String nama);

    @Query(value = "SELECT * FROM cuti_jenis WHERE LOWER(nama) = LOWER(:nama) AND is_deleted = true LIMIT 1", nativeQuery = true)
    Optional<CutiJenis> findDeletedByName(@Param("nama") String nama);
}
