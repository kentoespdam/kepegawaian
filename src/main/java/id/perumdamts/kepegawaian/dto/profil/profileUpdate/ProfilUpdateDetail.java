package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import lombok.Data;

import java.util.List;

@Data
public class ProfilUpdateDetail<T> {
    private ProfileUpdate profileUpdate;
    private T latestRevision;
    private T previousRevision;

    public static <T> ProfilUpdateDetail<T> build(ProfileUpdate profileUpdate, List<T> froms) {
        ProfilUpdateDetail<T> result = new ProfilUpdateDetail<>();
        result.setProfileUpdate(profileUpdate);

        result.setLatestRevision(froms.getFirst());
        result.setPreviousRevision(froms.getLast());
        return result;
    }
}
