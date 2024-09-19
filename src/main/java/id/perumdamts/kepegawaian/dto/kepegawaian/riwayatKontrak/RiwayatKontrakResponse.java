package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatKontrakResponse {
    private Long id;
    private String nipam;
    private String nama;
    private String nomorKontrak;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String notes;

    public static RiwayatKontrakResponse from(RiwayatKontrak entity) {
        RiwayatKontrakResponse response = new RiwayatKontrakResponse();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setNomorKontrak(entity.getNomorKontrak());
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setNotes(entity.getNotes());
        return response;
    }
}
