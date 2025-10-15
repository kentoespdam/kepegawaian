package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

public interface ProfileUpdateApprovalService {
    void changeKeluargaHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval);
}
