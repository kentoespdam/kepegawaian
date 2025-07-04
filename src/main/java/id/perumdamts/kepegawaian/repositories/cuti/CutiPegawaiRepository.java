package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface CutiPegawaiRepository extends JpaRepository<CutiPegawai, Long>,
        JpaSpecificationExecutor<CutiPegawai>,
        RevisionRepository<CutiPegawai, Long, Long> {
    Optional<CutiPegawai> findByIdAndApprovalCutiStatus(Long id, EApprovalCutiStatus eApprovalCutiStatus);
}
