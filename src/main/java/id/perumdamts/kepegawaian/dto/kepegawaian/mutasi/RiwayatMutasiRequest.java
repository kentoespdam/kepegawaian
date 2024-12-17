package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiRequest extends CommonPageRequest {
    private Long pegawaiId;
    private Long riwayatSkId;
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    private EJenisMutasi jenisMutasi;
    private Long organisasiId;
    private String namaOrganisasi;
    private Long jabatanId;
    private String namaJabatan;
    private Long organisasiLamaId;
    private String namaOrganisasiLama;
    private Long jabatanLamaId;
    private String namaJabatanLama;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecification() {
        Specification<RiwayatMutasi> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatMutasi> riwayatSkSpec = Objects.isNull(riwayatSkId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("riwayatSk").get("id"), riwayatSkId);
        Specification<RiwayatMutasi> skSpec = Objects.isNull(nomorSk) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("riwayatSk").get("nomorSk"), nomorSk);
        Specification<RiwayatMutasi> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<RiwayatMutasi> namaOrganisasiSpec = Objects.isNull(namaOrganisasi) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasi").get("nama"), namaOrganisasi);
        Specification<RiwayatMutasi> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<RiwayatMutasi> namaJabatanSpec = Objects.isNull(namaJabatan) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("nama"), namaJabatan);
        Specification<RiwayatMutasi> organisasiLamaSpec = Objects.isNull(organisasiLamaId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasiLama").get("id"), organisasiLamaId);
        Specification<RiwayatMutasi> namaOrganisasiLamaSpec = Objects.isNull(namaOrganisasiLama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasiLama").get("nama"), namaOrganisasiLama);
        Specification<RiwayatMutasi> jabatanLamaSpec = Objects.isNull(jabatanLamaId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatanLama").get("id"), jabatanLamaId);
        Specification<RiwayatMutasi> namaJabatanLamaSpec = Objects.isNull(namaJabatanLama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatanLama").get("nama"), namaJabatanLama);
        return Specification.where(pegawaiSpec).and(riwayatSkSpec).and(skSpec).and(organisasiSpec).and(namaOrganisasiSpec)
                .and(jabatanSpec).and(namaJabatanSpec).and(organisasiLamaSpec).and(namaOrganisasiLamaSpec)
                .and(jabatanLamaSpec).and(namaJabatanLamaSpec);
    }

}
