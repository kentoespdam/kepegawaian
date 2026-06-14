package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;

/**
 * Read-side DTO for Profesi used by cross-module consumers (e.g. PegawaiResponse).
 * Standalone Profesi detail with apdList/alatKerjaList has moved to {@link ProfesiDetail}
 * and is returned only by {@code GET /master/profesi/{id}} (JOOQ MULTISET).
 *
 * <p>TODO: migrate PegawaiResponse to use {@code ProfesiQuery} and remove this class.
 */
@Data
public class ProfesiResponse {
    private Long id;
    private String nama;
    private String detail;
    private String resiko;

    public static ProfesiResponse from(Profesi entity) {
        if (entity == null) return null;
        ProfesiResponse response = new ProfesiResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setDetail(entity.getDetail());
        response.setResiko(entity.getResiko());
        return response;
    }
}
