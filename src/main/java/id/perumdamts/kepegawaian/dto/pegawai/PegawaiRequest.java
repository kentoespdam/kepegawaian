package id.perumdamts.kepegawaian.dto.pegawai;


import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiRequest extends PageRequest {
    private String nipam;
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long jabatanId;
    private Long organisasiId;
    private Long profesiId;
    private Long golonganId;
    private Long gradeId;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;
    private EJenisKelamin jenisKelamin;

    @JsonIgnore
    public Specification<Pegawai> getSpecification() {
        return SpecificationBuilder.<Pegawai>of()
                .addLike(nipam, "nipam")
                .addLike(nik, "biodata", "nik")
                .addLike(nama, "biodata", "nama")
                .addEqual(statusPegawai, "statusPegawai")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(organisasiId, "organisasi", "id")
                .addEqual(profesiId, "profesi", "id")
                .addEqual(golonganId, "golongan", "id")
                .addEqual(gradeId, "grade", "id")
                .addEqual(statusKerja, "statusKerja")
                .addEqual(jenisKelamin, "biodata", "jenisKelamin")
                .build();
    }

    @JsonIgnore
    public Pageable getPageable() {
        if (sortBy == null || sortBy.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(getPageNumber(), getSizeOrDefault());
        }
        switch (sortBy) {
            case "nik" -> sortBy = "biodata.nik";
            case "nama" -> sortBy = "biodata.nama";
            case "jabatanId" -> sortBy = "jabatan.nama";
            case "organisasiId" -> sortBy = "organisasi.nama";
            case "profesiId" -> sortBy = "profesi.nama";
            case "golonganId" -> sortBy = "golongan.golongan";
            case "gradeId" -> sortBy = "grade.grade";
            case "jenisKelamin" -> sortBy = "biodata.jenisKelamin";
        }
        return org.springframework.data.domain.PageRequest.of(getPageNumber(), getSizeOrDefault(),
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    }
}
