package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Query parameter untuk riwayat penggajian mandiri (self-service)")
public class GajiBatchMasterSelfQuery extends PagedRequest {
    @Schema(description = "Periode penggajian (opsional, format YYYY-MM)", example = "2026-06")
    private String periode;

    @Schema(description = "Pencarian bebas", example = "Gaji")
    private String search;

    @Schema(description = "Filter status proses gaji minimal", example = "FINISHED")
    private EProsesGaji status;
}
