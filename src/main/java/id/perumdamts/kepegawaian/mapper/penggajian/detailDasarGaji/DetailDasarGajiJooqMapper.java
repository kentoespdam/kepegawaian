package id.perumdamts.kepegawaian.mapper.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.DasarGaji.DASAR_GAJI;
import static id.perumdamts.kepegawaian.jooq.tables.DetailDasarGaji.DETAIL_DASAR_GAJI;

public final class DetailDasarGajiJooqMapper {
    private DetailDasarGajiJooqMapper() {}

    public static DetailDasarGajiResponse mapToResponse(Record record) {
        if (record == null) return null;
        DasarGajiResponse dasarGaji = record.get(DETAIL_DASAR_GAJI.DASAR_GAJI_ID) != null
                ? new DasarGajiResponse(
                record.get(DETAIL_DASAR_GAJI.DASAR_GAJI_ID),
                record.get(DASAR_GAJI.DESKRIPSI),
                record.get(DASAR_GAJI.TANGGAL_AWAL),
                record.get(DASAR_GAJI.TANGGAL_AKHIR),
                record.get(DASAR_GAJI.AKTIF))
                : null;
        return new DetailDasarGajiResponse(
                record.get(DETAIL_DASAR_GAJI.ID),
                dasarGaji,
                record.get(DETAIL_DASAR_GAJI.MKG),
                record.get(DETAIL_DASAR_GAJI.GOLONGAN_KODE),
                record.get(DETAIL_DASAR_GAJI.NOMINAL)
        );
    }
}
