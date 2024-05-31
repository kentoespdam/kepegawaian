package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import lombok.Data;

import java.util.List;

@Data
public class KartuIdentitasMiniResponse {
    private Long id;
    private JenisKitasResponse jenisKartu;
    private String nomorKartu;

    public static KartuIdentitasMiniResponse from(KartuIdentitas entity) {
        JenisKitasResponse jenisKitas = JenisKitasResponse.from(entity.getJenisKartu());
        KartuIdentitasMiniResponse response = new KartuIdentitasMiniResponse();
        response.setId(entity.getId());
        response.setJenisKartu(jenisKitas);
        response.setNomorKartu(entity.getNomorKartu());
        return response;
    }

    public static List<KartuIdentitasMiniResponse> from(List<KartuIdentitas> kartuIdentitas) {
        return kartuIdentitas.stream().map(KartuIdentitasMiniResponse::from).toList();
    }
}
