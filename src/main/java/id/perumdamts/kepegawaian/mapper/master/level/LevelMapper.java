package id.perumdamts.kepegawaian.mapper.master.level;

import id.perumdamts.kepegawaian.dto.master.level.LevelPostRequest;
import id.perumdamts.kepegawaian.entities.master.Level;

public final class LevelMapper {
    private LevelMapper() {}

    public static Level toEntity(LevelPostRequest request) {
        return new Level(request.getNama());
    }

    public static void updateEntity(Level entity, LevelPostRequest request) {
        entity.setNama(request.getNama());
    }
}
