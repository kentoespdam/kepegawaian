package id.perumdamts.kepegawaian.dto.profil.keahlian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class KeahlianPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @Min(value = 1, message = "Keahlian ID is required")
    private Long keahlianId;
    @NotNull(message = "Kualifikasi is required")
    @Enumerated(EnumType.ORDINAL)
    private EKualifikasi kualifikasi;
    private Boolean sertifikasi = false;
    @NotEmpty(message = "Institusi is required")
    private String institusi;
    @Min(value = 1970, message = "Tahun is required")
    private Integer tahun;
    private String masaBerlaku;

    @JsonIgnore
    public Specification<Keahlian> getSpecification() {
        Specification<Keahlian> biodataSpec = (root, query, cb) ->
                cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Keahlian> keahlianSpec = (root, query, cb) ->
                cb.equal(root.get("jenisKeahlian").get("id"), keahlianId);
        Specification<Keahlian> tahunSpec = (root, query, cb) ->
                cb.equal(root.get("tahun"), tahun);
        return Specification.where(biodataSpec).and(keahlianSpec).and(tahunSpec);
    }

    public static Keahlian toEntity(KeahlianPostRequest request, Biodata biodata, JenisKeahlian jenisKeahlian) {
        Keahlian entity = new Keahlian();
        entity.setBiodata(biodata);
        entity.setJenisKeahlian(jenisKeahlian);
        entity.setKualifikasi(request.getKualifikasi());
        entity.setSertifikasi(request.getSertifikasi());
        entity.setInstitusi(request.getInstitusi());
        entity.setTahun(request.getTahun());
        entity.setMasaBerlaku(request.getMasaBerlaku());
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
