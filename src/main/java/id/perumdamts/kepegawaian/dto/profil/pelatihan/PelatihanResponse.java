package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PelatihanResponse {
    private Long id;
    private BiodataMiniResponse biodata;
    private JenisPelatihanResponse jenisPelatihan;
    private String nama;
    private String nilai;
    private Boolean lulus;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String notes;
    private Boolean disetujui;
    private String disetujuiOleh;

    public static PelatihanResponse from(Pelatihan entity) {
        BiodataMiniResponse biodata = BiodataMiniResponse.from(entity.getBiodata());
        JenisPelatihanResponse jenisPelatihan = JenisPelatihanResponse.from(entity.getJenisPelatihan());
        PelatihanResponse response = new PelatihanResponse();
        response.setId(entity.getId());
        response.setBiodata(biodata);
        response.setJenisPelatihan(jenisPelatihan);
        response.setNama(entity.getNama());
        response.setNilai(entity.getNilai());
        response.setLulus(entity.getLulus());
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setNotes(entity.getNotes());
        response.setDisetujui(entity.getDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        return response;
    }
}
