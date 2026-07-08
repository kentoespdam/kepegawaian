package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;

public record JenjangPendidikanMiniResponse(
        Long id,
        String nama
) {
    public static JenjangPendidikanMiniResponse from(JenjangPendidikan entity) {
        return new JenjangPendidikanMiniResponse(entity.getId(), entity.getNama());
    }
}
