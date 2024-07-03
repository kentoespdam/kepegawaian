package id.perumdamts.kepegawaian.dto.pegawai;


import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiRequest extends CommonPageRequest {
    private String nipam;
    private String nik;
    private String nama;
    private Long statusPegawaiId;
    private Long jabatanId;
    private Long organisasiId;
    private Long profesiId;
    private Long golonganId;
    private Long gradeId;
    private List<Long> statusPegawaiIds = new ArrayList<>();

    public Specification<Pegawai> getSpecification() {
        Specification<Pegawai> pegawaiSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nipam"), nipam);
        Specification<Pegawai> nikSpec = Objects.isNull(nik) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nik"), nik);
        Specification<Pegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nama"), "%" + nama + "%");
        Specification<Pegawai> statusPegawaiSpec = Objects.isNull(statusPegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusPegawai").get("id"), statusPegawaiId);
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
        Specification<Pegawai> statusPegawaiIdsSpec = (root, query, criteriaBuilder) -> {
            if (Objects.isNull(statusPegawaiIds) || statusPegawaiIds.isEmpty()) {
                statusPegawaiIds.add(5L);
                statusPegawaiIds.add(6L);
            }
            return criteriaBuilder.in(root.get("statusPegawai").get("id")).value(statusPegawaiIds).not();
        };

        return Specification.where(pegawaiSpec).and(nikSpec).and(namaSpec).and(statusPegawaiSpec)
                .and(jabatanSpec).and(organisasiSpec).and(profesiSpec).and(golonganSpec)
                .and(gradeSpec).and(statusPegawaiIdsSpec);
    }
}
