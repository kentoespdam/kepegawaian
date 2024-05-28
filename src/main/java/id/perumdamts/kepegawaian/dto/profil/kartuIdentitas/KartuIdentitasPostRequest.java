package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class KartuIdentitasPostRequest {
    @NotEmpty(message = "NIK tidak boleh kosong")
    private String nik;
    @Min(value = 1L, message = "Jenis kartu identitas tidak boleh kosong")
    private Long jenisKartu;
    @NotEmpty(message = "Nomor kartu identitas tidak boleh kosong")
    private String nomorKartu;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalExpired;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerima = LocalDate.of(1945, 8, 17);
    private String notes;

    @JsonIgnore
    public Specification<KartuIdentitas> getSpecification() {
        Specification<KartuIdentitas> nikSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("biodata").get("nik"), nik);
        Specification<KartuIdentitas> jenisKartuSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("jenisKartu").get("id"), jenisKartu);
        Specification<KartuIdentitas> nomorKartuSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nomorKartu"), nomorKartu);
        return Specification.where(nikSpec).and(jenisKartuSpec).and(nomorKartuSpec);
    }

    public static KartuIdentitas toEntity(
            KartuIdentitasPostRequest request,
            JenisKitas jenisKartu
    ) {
        Biodata biodata = new Biodata(request.getNik());
        KartuIdentitas entity = new KartuIdentitas();
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static KartuIdentitas toEntity(
            KartuIdentitasPostRequest request,
            Biodata biodata,
            JenisKitas jenisKartu
    ) {
        KartuIdentitas entity = new KartuIdentitas();
        entity.setBiodata(biodata);
        entity.setJenisKartu(jenisKartu);
        entity.setNomorKartu(request.getNomorKartu());
        entity.setTanggalExpired(request.getTanggalExpired());
        entity.setTanggalTerima(request.getTanggalTerima());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
