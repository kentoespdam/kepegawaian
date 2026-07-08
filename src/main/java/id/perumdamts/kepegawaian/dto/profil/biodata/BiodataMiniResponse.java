package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;

import java.time.LocalDate;

public record BiodataMiniResponse(
        String nik,
        String nama,
        EJenisKelamin jenisKelamin,
        EStatusKawin statusKawin,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalLahir,
        KartuIdentitasMiniResponse bpjs
) {
    public static BiodataMiniResponse from(Biodata biodata) {
        KartuIdentitasMiniResponse bpjs = null;
        if (biodata.getKartuIdentitas() != null) {
            bpjs = biodata.getKartuIdentitas().stream()
                    .filter(item -> item.getJenisKartu().getNama().equalsIgnoreCase("ASKES"))
                    .findFirst()
                    .map(KartuIdentitasMiniResponse::from)
                    .orElse(null);
        }
        return new BiodataMiniResponse(
                biodata.getNik(),
                biodata.getNama(),
                biodata.getJenisKelamin(),
                biodata.getStatusKawin(),
                biodata.getTanggalLahir(),
                bpjs
        );
    }
}
