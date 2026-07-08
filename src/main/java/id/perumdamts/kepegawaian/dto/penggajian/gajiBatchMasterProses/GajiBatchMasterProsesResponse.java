package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;

public record GajiBatchMasterProsesResponse(
        Long id,
        Long gajiBatchMasterId,
        String kode,
        Integer urut,
        String nama,
        EJenisGaji jenisGaji,
        Double nilai,
        String formula,
        String nilaiFormula
) {
    public static GajiBatchMasterProsesResponse from(GajiBatchMasterProses entity) {
        return new GajiBatchMasterProsesResponse(
                entity.getId(),
                entity.getBatchMasterId(),
                entity.getKode(),
                entity.getUrut(),
                entity.getNama(),
                entity.getJenisGaji(),
                entity.getNilai(),
                entity.getFormula(),
                entity.getNilaiFormula()
        );
    }
}
