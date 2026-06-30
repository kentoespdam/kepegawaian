package id.perumdamts.kepegawaian.mapper.cuti.jenis;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;

public final class CutiJenisMapper {
    private CutiJenisMapper() {}

    public static CutiJenis toEntity(CutiJenisPostRequest request, CutiJenis parent) {
        CutiJenis entity = new CutiJenis();
        entity.setParent(parent);
        entity.setNama(request.getNama());
        entity.setMaxHari(request.getMaxHari());
        entity.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
        return entity;
    }

    public static void updateEntity(CutiJenis entity, CutiJenisPutRequest request, CutiJenis parent) {
        entity.setParent(parent);
        entity.setNama(request.getNama());
        entity.setMaxHari(request.getMaxHari());
        entity.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
    }
}
