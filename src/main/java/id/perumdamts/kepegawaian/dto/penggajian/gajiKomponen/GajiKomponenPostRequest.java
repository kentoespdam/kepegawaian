package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class GajiKomponenPostRequest {
    private Integer urut;
    @Min(value = 1, message = "Profil Gaji ID is required")
    @NotNull(message = "Profil Gaji ID is required")
    private Long profilGajiId;
    @NotEmpty(message = "Kode is required")
    @NotNull(message = "Kode is required")
    private String kode;
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji = EJenisGaji.NONE;
    private Double nilai;
    private Boolean isReference = false;
    private String formula;

    @JsonIgnore
    public Specification<GajiKomponen> getSpecification() {
        Specification<GajiKomponen> profilGajiIdSpec = Objects.isNull(profilGajiId) ? null :
                (root, query, cb) -> cb.equal(root.get("profilGaji").get("id"), profilGajiId);
        Specification<GajiKomponen> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        return Specification.where(profilGajiIdSpec).and(kodeSpec);
    }

    public static GajiKomponen toEntity(GajiKomponenPostRequest request, GajiProfil profilGaji) {
        GajiKomponen entity = new GajiKomponen();
        entity.setUrut(request.getUrut());
        entity.setProfilGaji(profilGaji);
        entity.setKode(request.getKode().toUpperCase());
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        entity.setIsReference(request.getIsReference());
        if (request.getIsReference())
            entity.setFormula("#SYSTEM");
        else
            entity.setFormula(request.getFormula());
        return entity;
    }
}
