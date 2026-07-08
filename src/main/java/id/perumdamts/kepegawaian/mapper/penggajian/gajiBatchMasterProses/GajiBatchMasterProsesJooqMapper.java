package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMasterProses.GAJI_BATCH_MASTER_PROSES;

public final class GajiBatchMasterProsesJooqMapper {
    private GajiBatchMasterProsesJooqMapper() {}

    public static GajiBatchMasterProsesResponse mapToResponse(Record record) {
        if (record == null) return null;
        var jenisGajiObj = record.get(GAJI_BATCH_MASTER_PROSES.JENIS_GAJI);
        EJenisGaji jenisGaji = jenisGajiObj != null ? EJenisGaji.valueOf(jenisGajiObj.name()) : null;
        return new GajiBatchMasterProsesResponse(
                record.get(GAJI_BATCH_MASTER_PROSES.ID),
                record.get(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID),
                record.get(GAJI_BATCH_MASTER_PROSES.KODE),
                record.get(GAJI_BATCH_MASTER_PROSES.URUT),
                record.get(GAJI_BATCH_MASTER_PROSES.NAMA),
                jenisGaji,
                record.get(GAJI_BATCH_MASTER_PROSES.NILAI),
                record.get(GAJI_BATCH_MASTER_PROSES.FORMULA),
                record.get(GAJI_BATCH_MASTER_PROSES.NILAI_FORMULA)
        );
    }
}
