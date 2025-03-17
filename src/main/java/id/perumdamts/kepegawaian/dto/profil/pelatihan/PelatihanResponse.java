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
    private String lembaga;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private Boolean lulus;
    private String nilai;
    private Boolean ikatanDinas;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhirIkatan;
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
        response.setLembaga(entity.getLembaga());
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setLulus(entity.getLulus());
        response.setNilai(entity.getNilai());
        response.setIkatanDinas(entity.getIkatanDinas());
        response.setTanggalAkhirIkatan(entity.getTanggalAkhirIkatan());
        response.setNotes(entity.getNotes());
        response.setDisetujui(entity.getDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        return response;
    }
}
