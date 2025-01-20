package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DasarGajiResponse {
    private Long id;
    private String deskripsi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhir;
    private Boolean aktif;

    public static DasarGajiResponse from(DasarGaji entity) {
        DasarGajiResponse response = new DasarGajiResponse();
        response.setId(entity.getId());
        response.setDeskripsi(entity.getDeskripsi());
        response.setAktif(entity.isAktif());
        response.setTanggalAkhir(entity.getTanggalAkhir());
        response.setTanggalMulai(entity.getTanggalAwal());
        return response;
    }
}
