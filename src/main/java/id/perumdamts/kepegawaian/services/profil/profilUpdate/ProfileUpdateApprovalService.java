package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

public interface ProfileUpdateApprovalService {
    void changeHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval);
    void markAsStable(String revId);
    void resetEntityState(String id);
    void handleRejectedChange(ProfileUpdate profileUpdate, String revId);
    void revertToPreviousRevision(ProfileUpdate profileUpdate);
}
