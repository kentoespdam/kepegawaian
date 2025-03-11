package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class GajiBatchMasterProsesPostRequest {
    @Min(value = 1, message = "Master Batch ID required")
    @NotNull(message = "Master Batch ID required")
    private Long masterBatchId;
    private String nama;
    private EJenisGaji jenisGaji;
    private Double nilai;

    @JsonIgnore
    public Specification<GajiBatchMasterProses> getSpecification() {
        Specification<GajiBatchMasterProses> masterBatchIdSpec = Objects.isNull(masterBatchId) ? null :
                (root, query, cb) -> cb.equal(root.get("masterBatchId"), masterBatchId);
        Specification<GajiBatchMasterProses> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        Specification<GajiBatchMasterProses> jenisGajiSpec = Objects.isNull(jenisGaji) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisGaji"), jenisGaji);
        return Specification.where(masterBatchIdSpec).and(namaSpec).and(jenisGajiSpec);
    }

    public static GajiBatchMasterProses toEntity(GajiBatchMasterProsesPostRequest request) {
        GajiBatchMasterProses entity = new GajiBatchMasterProses();
        entity.setBatchMasterId(request.getMasterBatchId());
        entity.setKode("ADD_" + request.getNama());
        entity.setUrut(99);
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        return entity;
    }
}
