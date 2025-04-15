package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PengalamanKerjaResponse {
    private Long id;
    private BiodataMiniResponse biodata;
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;
    private Boolean disetujui;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPengajuan;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;

    public static PengalamanKerjaResponse from(PengalamanKerja entity) {
        BiodataMiniResponse biodata = BiodataMiniResponse.from(entity.getBiodata());
        PengalamanKerjaResponse response = new PengalamanKerjaResponse();
        response.setId(entity.getId());
        response.setBiodata(biodata);
        response.setNamaPerusahaan(entity.getNamaPerusahaan());
        response.setTypePerusahaan(entity.getTypePerusahaan());
        response.setJabatan(entity.getJabatan());
        response.setLokasi(entity.getLokasi());
        response.setTahunMasuk(entity.getTahunMasuk());
        response.setTahunKeluar(entity.getTahunKeluar());
        response.setNotes(entity.getNotes());
        response.setDisetujui(entity.getDisetujui());
        response.setTanggalPengajuan(entity.getTanggalPengajuan());
        response.setTanggalDisetujui(entity.getTanggalDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        return response;
    }
}
