package id.perumdamts.kepegawaian.dto.master.sanksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class SanksiPostRequest {
    @NotNull(message = "Kode is required")
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Keterangan is required")
    @NotEmpty(message = "Keterangan is required")
    private String keterangan;
    @NotNull(message = "Jenis SP is required")
    @Min(value = 1, message = "Jenis SP is required")
    private Long jenisSpId;
    private Boolean potTkk;
    private Integer jmlPotTkk;
    private Boolean isPendingPangkat;
    private Boolean isPendingGaji;
    private Boolean isTurunPangkat;
    private Boolean isTurunJabatan;
    private Boolean isSuspension;
    private Boolean isTerminateDh;
    private Boolean isTerminateTh;

    @JsonIgnore
    public Specification<Sanksi> getSpecification() {
        Specification<Sanksi> kodeSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("kode"), kode);
        Specification<Sanksi> namaSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("keterangan"), keterangan);
        Specification<Sanksi> jenisSpSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("jenisSp").get("id"), jenisSpId);
        return Specification.where(kodeSpec).and(namaSpec).and(jenisSpSpec);
    }

    public static Sanksi toEntity(SanksiPostRequest request, JenisSp jenisSp) {
        Sanksi sanksi = new Sanksi();
        sanksi.setKode(request.getKode());
        sanksi.setKeterangan(request.getKeterangan());
        sanksi.setJenisSp(jenisSp);
        sanksi.setPotTkk(request.getPotTkk());
        sanksi.setJmlPotTkk(request.getJmlPotTkk());
        sanksi.setIsPendingPangkat(request.getIsPendingPangkat());
        sanksi.setIsPendingGaji(request.getIsPendingGaji());
        sanksi.setIsTurunPangkat(request.getIsTurunPangkat());
        sanksi.setIsTurunJabatan(request.getIsTurunJabatan());
        sanksi.setIsSuspension(request.getIsSuspension());
        sanksi.setIsTerminateDh(request.getIsTerminateDh());
        sanksi.setIsTerminateTh(request.getIsTerminateTh());
        return sanksi;
    }
}
