package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;

import java.util.List;

public record JenjangPendidikanResponse(
        Long id,
        String nama,
        String shortName,
        Integer seq,
        Boolean isStatistik
) {
    public static JenjangPendidikanResponse from(JenjangPendidikan entity) {
        if (entity == null) return null;
        return new JenjangPendidikanResponse(
                entity.getId(),
                entity.getNama(),
                entity.getShortName(),
                entity.getSeq(),
                entity.getIsStatistik()
        );
    }

    public static List<JenjangPendidikanResponse> from(List<JenjangPendidikan> entities) {
        return entities.stream().map(JenjangPendidikanResponse::from).toList();
    }
}
