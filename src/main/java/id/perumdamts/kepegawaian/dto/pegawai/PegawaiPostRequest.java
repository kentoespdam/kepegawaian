package id.perumdamts.kepegawaian.dto.pegawai;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class PegawaiPostRequest {
    @NotEmpty(message = "NIP is required")
    private String nipam;
    @NotEmpty(message = "NIK is required")
    private String nik;
    @NotNull(message = "Status Pegawai is required")
    @Min(value = 1, message = "Status Pegawai is required")
    private Long statusPegawaiId;
    @NotNull(message = "Jabatan is required")
    @Min(value = 1, message = "Jabatan is required")
    private Long jabatanId;
    @NotNull(message = "Organisasi is required")
    @Min(value = 1, message = "Organisasi is required")
    private Long organisasiId;
    @NotNull(message = "Profesi is required")
    @Min(value = 1, message = "Profesi is required")
    private Long profesiId;
    @NotNull(message = "Golongan is required")
    @Min(value = 1, message = "Golongan is required")
    private Long golonganId;
    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade is required")
    private Long gradeId;
    @NotNull(message = "Status Kerja is required")
    @Min(value = 1, message = "Status Kerja is required")
    private Long statusKerjaId;
    private String notes;

    @JsonIgnore
    public Specification<Pegawai> getSpecification() {
        Specification<Pegawai> pegawaiSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nipam"), nipam);
        Specification<Pegawai> nikSpec = Objects.isNull(nik) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("biodata").get("nik"), nik);

        return Specification.where(pegawaiSpec).and(nikSpec);
    }

    public static Pegawai toEntity(
            PegawaiPostRequest request,
            Biodata biodata,
            StatusPegawai statusPegawai,
            Jabatan jabatan,
            Organisasi organisasi,
            Profesi profesi,
            Golongan golongan,
            Grade grade,
            StatusKerja statusKerja
    ) {
        Pegawai entity = new Pegawai();
        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        entity.setStatusPegawai(statusPegawai);
        entity.setJabatan(jabatan);
        entity.setOrganisasi(organisasi);
        entity.setProfesi(profesi);
        entity.setGolongan(golongan);
        entity.setGrade(grade);
        entity.setStatusKerja(statusKerja);

        return entity;
    }


}
