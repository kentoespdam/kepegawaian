package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

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
        return SpecificationBuilder.<Profesi>of()
                .addEqual(nama, "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(gradeId, "grade", "id")
                .build();
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
