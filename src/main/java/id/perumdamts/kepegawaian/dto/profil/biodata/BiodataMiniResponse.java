package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BiodataMiniResponse {
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private KartuIdentitasMiniResponse bpjs;

    public static BiodataMiniResponse from(Biodata biodata) {
        BiodataMiniResponse response = new BiodataMiniResponse();
        response.setNik(biodata.getNik());
        response.setNama(biodata.getNama());
        response.setJenisKelamin(biodata.getJenisKelamin());
        response.setStatusKawin(biodata.getStatusKawin());
        response.setTanggalLahir(biodata.getTanggalLahir());

        if (biodata.getKartuIdentitas() != null)
            biodata.getKartuIdentitas().stream()
                    .filter(item -> item.getJenisKartu().getNama().equalsIgnoreCase("ASKES"))
                    .findFirst()
                    .ifPresent(item -> response.setBpjs(KartuIdentitasMiniResponse.from(item)));

        return response;
    }
}
