package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

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
    private String kota;
    private Integer tahunMasuk;
    private Boolean isLulus;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest = false;
    private Boolean changed_status = true;


    @JsonIgnore
    public Specification<Pendidikan> getSpecification() {
        return SpecificationBuilder.<Pendidikan>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(jenjangPendidikanId, "jenjangPendidikan", "id")
                .build();
    }

    public static Pendidikan from(PendidikanPostRequest request, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        Pendidikan entity = new Pendidikan();
        entity.setBiodata(biodata);
        entity.setJenjangPendidikan(jenjangPendidikan);
        entity.setGelarDepan(request.getGelarDepan());
        entity.setGelarBelakang(request.getGelarBelakang());
        entity.setJurusan(request.getJurusan());
        entity.setInstitusi(request.getInstitusi());
        entity.setKota(request.getKota());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setIsLulus(request.getIsLulus());
        entity.setTahunLulus(request.getTahunLulus());
        entity.setGpa(request.getGpa());
        entity.setIsLatest(request.getIsLatest());
        entity.setChangedStatus(request.getChanged_status());
        return entity;
    }
}
