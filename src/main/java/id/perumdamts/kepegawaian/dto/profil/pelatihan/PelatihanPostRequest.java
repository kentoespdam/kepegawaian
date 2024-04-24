package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PelatihanPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @Min(value = 1, message = "Jenis Pelatihan ID is required")
    private Long jenisPelatihanId;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotEmpty(message = "Nilai is required")
    private String nilai;
    private Boolean lulus=true;
    @NotNull(message = "Tanggal Mulai is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @NotNull(message = "Tanggal Selesai is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String notes;

    public Specification<Pelatihan> getSpecification() {
        Specification<Pelatihan> biodataSpec = (root, query, cb) ->
                cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Pelatihan> pelatihanSpec = (root, query, cb) ->
                cb.equal(root.get("jenisPelatihan").get("id"), jenisPelatihanId);
        Specification<Pelatihan> namaSpec = (root, query, cb) ->
                cb.equal(root.get("nama"),  nama);
        return Specification.where(biodataSpec).and(pelatihanSpec).and(namaSpec);
    }

    public static Pelatihan toEntity(PelatihanPostRequest request, Biodata biodata, JenisPelatihan jenisPelatihan) {
        Pelatihan entity = new Pelatihan();
        entity.setBiodata(biodata);
        entity.setJenisPelatihan(jenisPelatihan);
        entity.setNama(request.getNama());
        entity.setNilai(request.getNilai());
        entity.setLulus(request.getLulus());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setNotes(request.getNotes());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
