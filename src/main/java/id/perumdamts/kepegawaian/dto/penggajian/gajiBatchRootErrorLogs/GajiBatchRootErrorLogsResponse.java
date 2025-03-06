package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLogs;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import lombok.Data;

import java.util.List;

@Data
public class GajiBatchRootErrorLogsResponse {
    private Long id;
    private String batchId;
    private String nipam;
    private String nama;
    private String notes;

    public static GajiBatchRootErrorLogsResponse from(GajiBatchRootErrorLogs entity) {
        GajiBatchRootErrorLogsResponse response = new GajiBatchRootErrorLogsResponse();
        response.setId(entity.getId());
        response.setBatchId(entity.getGajiBatchRoot().getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }

    public static List<GajiBatchRootErrorLogsResponse> from(List<GajiBatchRootErrorLogs> entities) {
        return entities.stream().map(GajiBatchRootErrorLogsResponse::from).toList();
    }
}
