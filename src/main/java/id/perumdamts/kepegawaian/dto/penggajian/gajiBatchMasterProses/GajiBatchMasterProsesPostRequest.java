package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiBatchMasterProsesPostRequest {
    @Min(value = 1, message = "Master Batch ID required")
    @NotNull(message = "Master Batch ID required")
    private Long batchMasterId;
    private String nama;
    private EJenisGaji jenisGaji;
    private Double nilai;

    @JsonIgnore
    public Specification<GajiBatchMasterProses> getSpecification() {
        return SpecificationBuilder.<GajiBatchMasterProses>of()
                .addEqual(batchMasterId, "batchMasterId")
                .addEqual(nama, "nama")
                .addEqual(jenisGaji, "jenisGaji")
                .build();
    }

    public static GajiBatchMasterProses toEntity(GajiBatchMasterProsesPostRequest request) {
        GajiBatchMasterProses entity = new GajiBatchMasterProses();
        entity.setBatchMasterId(request.getBatchMasterId());
        entity.setKode("ADD_" + request.getNama().replace(" ", "_"));
        entity.setUrut(99);
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        return entity;
    }
}
