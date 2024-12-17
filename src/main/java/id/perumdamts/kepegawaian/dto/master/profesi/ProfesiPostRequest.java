package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class ProfesiPostRequest {
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Jabatan ID must be greater than or equal to 1")
    private Long jabatanId;
    @Min(value = 1, message = "Grade ID must be greater than or equal to 1")
    private Long gradeId;
    @NotEmpty(message = "Nama Profesi is required")
    private String nama;
    @NotEmpty(message = "Detail Profesi is required")
    private String detail;
    @NotEmpty(message = "Resiko Profesi is required")
    private String resiko;

    @JsonIgnore
    public Specification<Profesi> getSpecification() {
        Specification<Profesi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        Specification<Profesi> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, cb) -> cb.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<Profesi> gradeSpec = Objects.isNull(gradeId) ? null :
                (root, query, cb) -> cb.equal(root.get("grade").get("id"), gradeId);
        return Specification.where(namaSpec).and(jabatanSpec).and(gradeSpec);
    }

    public static Profesi toEntity(ProfesiPostRequest request, Organisasi organisasi, Jabatan jabatan, Grade grade) {
        Profesi entity = new Profesi();
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        entity.setLevel(jabatan.getLevel());
        entity.setGrade(grade);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
        return entity;
    }
}
