package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.*;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class ProfilKeluargaPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
//    @NotEmpty(message = "Nik is required")
    private String nik;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotNull(message = "Jenis Kelamin is required")
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    @NotNull(message = "Agama is required")
    @Enumerated(EnumType.ORDINAL)
    private EAgama agama;
    @NotNull(message = "Hubungan Keluarga is required")
    @Enumerated(EnumType.ORDINAL)
    private EHubunganKeluarga hubunganKeluarga;
    @NotEmpty(message = "Tempat Lahir is required")
    private String tempatLahir;
    @NotNull(message = "Tanggal Lahir is required")
    private LocalDate tanggalLahir;
    @NotNull(message = "Tanggungan is required")
    private Boolean tanggungan;
    private Long pendidikanId;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPendidikan statusPendidikan = EStatusPendidikan.SEKOLAH;
    @NotNull(message = "Status Kawin is required")
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String notes;

    @JsonIgnore
    public Specification<ProfilKeluarga> getSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("biodata").get("nik"), biodataId),
                        criteriaBuilder.equal(root.get("nik"), nik),
                        criteriaBuilder.equal(root.get("nama"), nama),
                        criteriaBuilder.equal(root.get("jenisKelamin"), jenisKelamin),
                        criteriaBuilder.equal(root.get("hubunganKeluarga"), hubunganKeluarga)
                );
    }

    public static ProfilKeluarga toEntity(
            ProfilKeluargaPostRequest request,
            Biodata biodata,
            JenjangPendidikan pendidikan
    ) {
        ProfilKeluarga entity = new ProfilKeluarga();
        entity.setBiodata(biodata);
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setJenisKelamin(request.getJenisKelamin());
        entity.setAgama(request.getAgama());
        entity.setHubunganKeluarga(request.getHubunganKeluarga());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setTanggungan(request.getTanggungan());
        if (pendidikan != null) entity.setPendidikan(pendidikan);
        entity.setStatusPendidikan(request.getStatusPendidikan());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
