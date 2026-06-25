package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class BiodataPostRequest {
    @NotEmpty(message = "NIK is required")
    private String nik;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotNull(message = "Jenis Kelamin is required")
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    @NotEmpty(message = "Tempat Lahir is required")
    private String tempatLahir;
    @NotNull(message = "Tanggal Lahir is required")
    private LocalDate tanggalLahir;
    @NotEmpty(message = "Alamat is required")
    private String alamat;
    private String telp;
    @NotNull(message = "Agama is required")
    @Enumerated(value = EnumType.ORDINAL)
    private EAgama agama;
    @NotEmpty(message = "Ibu Kandung is required")
    private String ibuKandung;
    @Min(value = 1L, message = "Pendidikan Terakhir is required")
    private Long pendidikanTerakhirId;
    @Enumerated(value = EnumType.STRING)
    private EGolonganDarah golonganDarah;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String notes;
    private Boolean isPegawai = false;

    @JsonIgnore
    public Specification<Biodata> getSpecification() {
        return SpecificationBuilder.<Biodata>of()
                .addEqual(nik, "nik")
                .addEqual(nama, "nama")
                .addEqual(tempatLahir, "tempatLahir")
                .addEqual(tanggalLahir, "tanggalLahir")
                .build();
    }
}
