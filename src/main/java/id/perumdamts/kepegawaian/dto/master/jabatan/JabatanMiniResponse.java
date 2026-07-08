package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;

public record JabatanMiniResponse(Long id, String kode, LevelResponse level, String nama) {
    public static JabatanMiniResponse from(Jabatan entity) {
        if (entity == null) return null;
        return new JabatanMiniResponse(
                entity.getId(),
                entity.getKode(),
                LevelMapper.toResponse(entity.getLevel()),
                entity.getNama()
        );
    }
}
