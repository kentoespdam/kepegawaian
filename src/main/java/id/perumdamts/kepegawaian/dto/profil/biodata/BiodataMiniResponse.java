package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import lombok.Data;

@Data
public class BiodataMiniResponse {
    private String nik;
    private String nama;

    public static BiodataMiniResponse from(Biodata biodata) {
        BiodataMiniResponse response = new BiodataMiniResponse();
        response.setNik(biodata.getNik());
        response.setNama(biodata.getNama());
        return response;
    }
}
