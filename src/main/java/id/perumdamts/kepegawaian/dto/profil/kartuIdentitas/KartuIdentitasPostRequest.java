package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
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
    private Long jenisKartuId;
    @NotEmpty(message = "Nomor kartu identitas tidak boleh kosong")
    private String nomorKartu;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalExpired;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerima = LocalDate.of(1945, 8, 17);
    private String notes;

    @JsonIgnore
    public Specification<KartuIdentitas> getSpecification() {
        return SpecificationBuilder.<KartuIdentitas>of()
                .addEqual(nik, "biodata", "nik")
                .addEqual(jenisKartuId, "jenisKartu", "id")
                .addEqual(nomorKartu, "nomorKartu")
                .build();
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
