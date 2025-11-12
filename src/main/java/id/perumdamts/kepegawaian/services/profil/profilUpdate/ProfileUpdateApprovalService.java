package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

public interface ProfileUpdateApprovalService {
    void changeHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval);
    void markAsStable(Long revId);
    void resetEntityState(Long id);
    void handleRejectedChange(ProfileUpdate profileUpdate, Long revId);
    void revertToPreviousRevision(ProfileUpdate profileUpdate);
}
