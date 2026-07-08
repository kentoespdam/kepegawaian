package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;

import java.util.List;

public record JenisKitasResponse(
        Long id,
        String nama
) {
    public static JenisKitasResponse from(JenisKitas entity) {
        return new JenisKitasResponse(entity.getId(), entity.getNama());
    }

    public static List<JenisKitasResponse> from(List<JenisKitas> entities) {
        return entities.stream().map(JenisKitasResponse::from).toList();
    }
}
