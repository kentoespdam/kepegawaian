package id.perumdamts.kepegawaian.mapper.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;

public final class JenisKitasMapper {
    private JenisKitasMapper() {}

    public static JenisKitas toEntity(JenisKitasPostRequest request) {
        return new JenisKitas(request.getNama());
    }

    public static void updateEntity(JenisKitas entity, JenisKitasPostRequest request) {
        entity.setNama(request.getNama());
    }
}
