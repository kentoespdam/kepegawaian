package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

public record GajiKpiUploadResponse(
    String periode,
    int totalRows,
    int inserted,
    int updated
) {}
