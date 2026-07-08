package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLog;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;

public record GajiBatchRootErrorLogsResponse(
        Long id,
        String nipam,
        String nama,
        String notes
) {
    public static GajiBatchRootErrorLogsResponse from(GajiBatchRootErrorLogs entity) {
        return new GajiBatchRootErrorLogsResponse(
                entity.getId(),
                entity.getNipam(),
                entity.getNama(),
                entity.getNotes()
        );
    }
}
