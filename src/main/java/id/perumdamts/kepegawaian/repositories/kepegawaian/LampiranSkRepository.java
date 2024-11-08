package id.perumdamts.kepegawaian.repositories.kepegawaian;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.penggajian.LampiranSk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;

public interface LampiranSkRepository extends JpaRepository<LampiranSk, Long>,
        JpaSpecificationExecutor<LampiranSk>,
        RevisionRepository<LampiranSk, Long, Long> {
    List<LampiranSk> findByRefAndRefId(EJenisSk jenisSk, Long id);

    List<LampiranSk> findAllByRefId(Long id);
}
