package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

import lombok.Data;

@Data
public class GajiKpiListRequest {
    private String search;
    private String periode;
    private Long organisasiId;
}
