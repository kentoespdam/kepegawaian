package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class KartuIdentitasResponse {
    private Long id;
    private String nik;
    private JenisKitasResponse jenisKartu;
    private String nomorKartu;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalExpired;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerima;
    private String notes;

    public static KartuIdentitasResponse from(KartuIdentitas entity) {
        KartuIdentitasResponse response = new KartuIdentitasResponse();
        response.setId(entity.getId());
        response.setNik(entity.getBiodata().getNik());
        response.setJenisKartu(JenisKitasResponse.from(entity.getJenisKartu()));
        response.setNomorKartu(entity.getNomorKartu());
        response.setTanggalExpired(entity.getTanggalExpired());
        response.setTanggalTerima(entity.getTanggalTerima());
        response.setNotes(entity.getNotes());
        return response;
    }

    public static List<KartuIdentitasResponse> from(List<KartuIdentitas> entities) {
        return entities.stream().map(KartuIdentitasResponse::from).toList();
    }
}
