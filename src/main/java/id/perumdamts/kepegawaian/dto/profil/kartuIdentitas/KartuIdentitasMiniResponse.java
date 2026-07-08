package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;

import java.util.List;

public record KartuIdentitasMiniResponse(
        Long id,
        JenisKitasResponse jenisKartu,
        String nomorKartu
) {
    public static KartuIdentitasMiniResponse from(KartuIdentitas entity) {
        return new KartuIdentitasMiniResponse(
                entity.getId(),
                JenisKitasResponse.from(entity.getJenisKartu()),
                entity.getNomorKartu()
        );
    }

    public static List<KartuIdentitasMiniResponse> from(List<KartuIdentitas> kartuIdentitas) {
        return kartuIdentitas.stream().map(KartuIdentitasMiniResponse::from).toList();
    }
}
