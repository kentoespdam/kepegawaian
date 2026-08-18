package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;

public record CutiJenisMiniResponse(
        Long id,
        String nama,
        Long parentId
) {
    public static CutiJenisMiniResponse from(CutiJenis entity) {
        return new CutiJenisMiniResponse(
                entity.getId(),
                entity.getNama(),
                entity.getParent() != null ? entity.getParent().getId() : null
        );
    }
}
