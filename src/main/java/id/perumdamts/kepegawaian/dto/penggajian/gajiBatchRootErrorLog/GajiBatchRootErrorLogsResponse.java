package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLog;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import lombok.Data;

@Data
public class GajiBatchRootErrorLogsResponse {
    private Long id;
    private String nipam;
    private String nama;
    private String notes;

    public static GajiBatchRootErrorLogsResponse from(GajiBatchRootErrorLogs entity) {
        GajiBatchRootErrorLogsResponse response = new GajiBatchRootErrorLogsResponse();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }
}
