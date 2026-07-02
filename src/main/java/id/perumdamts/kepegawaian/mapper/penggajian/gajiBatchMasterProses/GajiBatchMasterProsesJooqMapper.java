package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMasterProses.GAJI_BATCH_MASTER_PROSES;

public final class GajiBatchMasterProsesJooqMapper {
    private GajiBatchMasterProsesJooqMapper() {}

    public static GajiBatchMasterProsesResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiBatchMasterProsesResponse response = new GajiBatchMasterProsesResponse();
        response.setId(record.get(GAJI_BATCH_MASTER_PROSES.ID));
        response.setGajiBatchMasterId(record.get(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID));
        response.setKode(record.get(GAJI_BATCH_MASTER_PROSES.KODE));
        response.setUrut(record.get(GAJI_BATCH_MASTER_PROSES.URUT));
        response.setNama(record.get(GAJI_BATCH_MASTER_PROSES.NAMA));

        var jenisGaji = record.get(GAJI_BATCH_MASTER_PROSES.JENIS_GAJI);
        if (jenisGaji != null) {
            response.setJenisGaji(EJenisGaji.valueOf(jenisGaji.name()));
        }

        response.setNilai(record.get(GAJI_BATCH_MASTER_PROSES.NILAI));
        response.setFormula(record.get(GAJI_BATCH_MASTER_PROSES.FORMULA));
        response.setNilaiFormula(record.get(GAJI_BATCH_MASTER_PROSES.NILAI_FORMULA));

        return response;
    }
}
