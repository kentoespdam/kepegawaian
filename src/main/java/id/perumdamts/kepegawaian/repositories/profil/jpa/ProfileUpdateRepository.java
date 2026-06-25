package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProfileUpdateRepository extends JpaRepository<ProfileUpdate, Long>,
        JpaSpecificationExecutor<ProfileUpdate> {
    Optional<ProfileUpdate> findByIdAndApprovalStatus(Long id, EProfileUpdateApproval approvalStatus);
}