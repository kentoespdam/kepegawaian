package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PelatihanPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @Min(value = 1, message = "Jenis Pelatihan ID is required")
    private Long jenisPelatihanId;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotEmpty(message = "Lembaga is required")
    private String lembaga;
    @NotNull(message = "Tanggal Mulai is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @NotNull(message = "Tanggal Selesai is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private Boolean lulus = true;
    @NotEmpty(message = "Nilai is required")
    private String nilai;
    private Boolean ikatanDinas = false;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhirIkatan;
    private String notes;

    public static Pelatihan toEntity(PelatihanPostRequest request, Biodata biodata, JenisPelatihan jenisPelatihan) {
        Pelatihan entity = new Pelatihan();
        entity.setBiodata(biodata);
        entity.setJenisPelatihan(jenisPelatihan);
        entity.setNama(request.getNama());
        entity.setLembaga(request.getLembaga());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setLulus(request.getLulus());
        entity.setNilai(request.getNilai());
        entity.setIkatanDinas(request.getIkatanDinas());
        entity.setTanggalAkhirIkatan(request.getTanggalAkhirIkatan());
        entity.setNotes(request.getNotes());
        return entity;
    }
}