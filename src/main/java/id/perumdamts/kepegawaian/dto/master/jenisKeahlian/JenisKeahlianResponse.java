package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;

import java.util.List;

public record JenisKeahlianResponse(Long id, String nama) {
    public static JenisKeahlianResponse from(JenisKeahlian entity) {
        if (entity == null) return null;
        return new JenisKeahlianResponse(entity.getId(), entity.getNama());
    }

    public static List<JenisKeahlianResponse> from(List<JenisKeahlian> entities) {
        return entities.stream().map(JenisKeahlianResponse::from).toList();
    }
}
