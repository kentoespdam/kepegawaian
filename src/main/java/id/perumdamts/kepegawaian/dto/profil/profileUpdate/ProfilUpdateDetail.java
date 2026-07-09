package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

import java.util.List;

public record ProfilUpdateDetail<T>(
        ProfileUpdateQuery profileUpdate,
        T latestRevision,
        T previousRevision
) {
    public static <T> ProfilUpdateDetail<T> build(ProfileUpdate entity, List<T> froms) {
        return new ProfilUpdateDetail<>(
                ProfileUpdateQuery.from(entity),
                froms.getFirst(),
                froms.getLast()
        );
    }
}
