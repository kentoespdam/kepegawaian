package id.perumdamts.kepegawaian.dto.pegawai;


import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiRequest extends CommonPageRequest {
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

    public Specification<Pegawai> getSpecification() {
        Specification<Pegawai> pegawaiSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nipam"), nipam);
        Specification<Pegawai> nikSpec = Objects.isNull(nik) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nik"), nik);
        Specification<Pegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nama"), "%" + nama + "%");
        Specification<Pegawai> statusPegawaiSpec = Objects.isNull(statusPegawai) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusPegawai"), statusPegawai);
        Specification<Pegawai> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<Pegawai> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Pegawai> profesiSpec = Objects.isNull(profesiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profesi").get("id"), profesiId);
        Specification<Pegawai> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("golongan").get("id"), golonganId);
        Specification<Pegawai> gradeSpec = Objects.isNull(gradeId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("grade").get("id"), gradeId);
        Specification<Pegawai> statusPegawaiIdsSpec = Objects.isNull(statusKerja) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusKerja"), statusKerja);

        return Specification.where(pegawaiSpec).or(namaSpec).and(nikSpec).and(statusPegawaiSpec)
                .and(jabatanSpec).and(organisasiSpec).and(profesiSpec).and(golonganSpec)
                .and(gradeSpec).and(statusPegawaiIdsSpec);
    }

    @JsonIgnore
    @Override
    public Pageable getPageable() {
        if (sortBy == null || sortBy.isEmpty()) {
            return PageRequest.of(page, size);
        }
        switch (sortBy) {
            case "nik" -> sortBy = "biodata.nik";
            case "nama" -> sortBy = "biodata.nama";
            case "jabatanId" -> sortBy = "jabatan.nama";
            case "organisasiId" -> sortBy = "organisasi.nama";
            case "profesiId" -> sortBy = "profesi.nama";
            case "golonganId" -> sortBy = "golongan.golongan";
            case "gradeId" -> sortBy = "grade.grade";
        }
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    }
}
