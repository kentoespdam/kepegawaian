package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class PendidikanPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @Min(value = 1, message = "Jenjang Pendidikan ID is required")
    private Long jenjangPendidikanId;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    @NotEmpty(message = "Institusi is required")
    private String institusi;
    @Min(value = 1970, message = "Tahun Masuk is required")
    private Integer tahunMasuk;
    @Min(value = 1970, message = "Tahun Lulus is required")
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest=false;

    public static Pendidikan from(PendidikanPostRequest request, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        Pendidikan entity = new Pendidikan();
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjangPendidikan);
        entity.setGelarDepan(request.gelarDepan);
        entity.setGelarBelakang(request.gelarBelakang);
        entity.setJurusan(request.jurusan);
        entity.setInstitusi(request.institusi);
        entity.setTahunMasuk(request.tahunMasuk);
        entity.setTahunLulus(request.tahunLulus);
        entity.setGpa(request.gpa);
        entity.setIsLatest(request.isLatest);
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }

    public Specification<Pendidikan> getSpecification() {
        Specification<Pendidikan> biodataSpec= (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Pendidikan> jenjangSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("jenjangPendidikan").get("id"), jenjangPendidikanId);
        Specification<Pendidikan> gelarDepanSpec = Objects.isNull(gelarDepan) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gelarDepan"), gelarDepan);
        Specification<Pendidikan> gelarBelakangSpec = Objects.isNull(gelarBelakang) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gelarBelakang"), gelarBelakang);
        Specification<Pendidikan> jurusanSpec = Objects.isNull(jurusan) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jurusan"), jurusan);
        Specification<Pendidikan> institusiSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("institusi"), institusi);
        Specification<Pendidikan> tahunMasukSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tahunMasuk"), tahunMasuk);
        Specification<Pendidikan> tahunLulusSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tahunLulus"), tahunLulus);
        Specification<Pendidikan> gpaSpec = Objects.isNull(gpa) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gpa"), gpa);
        Specification<Pendidikan> isLatestSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isLatest"), isLatest);
        return Specification.where(biodataSpec).and(jenjangSpec).and(gelarDepanSpec)
                .and(gelarBelakangSpec).and(jurusanSpec).and(institusiSpec)
                .and(tahunMasukSpec).and(tahunLulusSpec).and(gpaSpec).and(isLatestSpec);
    }
}
