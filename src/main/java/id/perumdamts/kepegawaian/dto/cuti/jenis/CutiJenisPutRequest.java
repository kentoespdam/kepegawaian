package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import jakarta.annotation.Nullable;

public class CutiJenisPutRequest extends CutiJenisPostRequest {
    public static CutiJenis toEntity(CutiJenis entity, CutiJenisPutRequest request, @Nullable CutiJenis parent) {
        entity.setParent(parent);
        entity.setNama(request.getNama());
        entity.setMaxHari(request.getMaxHari());
        entity.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
        return entity;
    }
}
