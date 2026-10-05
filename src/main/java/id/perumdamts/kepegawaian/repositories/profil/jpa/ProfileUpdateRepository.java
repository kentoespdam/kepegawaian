package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileUpdateRepository extends JpaRepository<ProfileUpdate, Long> {
    Optional<ProfileUpdate> findByIdAndApprovalStatus(Long id, EProfileUpdateApproval approvalStatus);
}