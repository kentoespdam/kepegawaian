package id.perumdamts.kepegawaian.dto.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiPostRequest extends BiodataPostRequest {
    @NotEmpty(message = "Nipam is required")
    private String nipam;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;
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
    private String nomorSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlakuSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKontrakSelesai;
    private Double gajiPokok;
    private String notes;

    @JsonIgnore
    public Specification<Pegawai> getSpecificationPegawai() {
        Specification<Pegawai> pegawaiSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nipam"), nipam);
        return Specification.where(pegawaiSpec);
    }

    public static Pegawai toEntity(
            PegawaiPostRequest request,
            Biodata biodata,
            Jabatan jabatan,
            Organisasi organisasi,
            Profesi profesi,
            Golongan golongan,
            Grade grade
    ) {
        int umur = LocalDate.now().getYear() - biodata.getTanggalLahir().getYear();
        LocalDate pensiun = biodata.getTanggalLahir().plusYears(56 - umur);
        pensiun=LocalDate.of(pensiun.getYear(), pensiun.getMonth(), 1);

        Pegawai entity = new Pegawai();
        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        entity.setStatusPegawai(request.getStatusPegawai());
        entity.setJabatan(jabatan);
        entity.setOrganisasi(organisasi);
        entity.setProfesi(profesi);
        entity.setGolongan(golongan);
        entity.setGrade(grade);
        entity.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        entity.setTmtKerja(request.getTmtBerlakuSk());
        entity.setTmtPensiun(pensiun);
        entity.setGajiPokok(request.getGajiPokok());
        entity.setNotes(request.getNotes());

        return entity;
    }


}
