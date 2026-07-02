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
        DetailDasarGajiResponse response = new DetailDasarGajiResponse();
        response.setId(record.get(DETAIL_DASAR_GAJI.ID));
        response.setMkg(record.get(DETAIL_DASAR_GAJI.MKG));
        response.setGolonganKode(record.get(DETAIL_DASAR_GAJI.GOLONGAN_KODE));
        response.setNominal(record.get(DETAIL_DASAR_GAJI.NOMINAL));

        if (record.get(DETAIL_DASAR_GAJI.DASAR_GAJI_ID) != null) {
            DasarGajiResponse dg = new DasarGajiResponse();
            dg.setId(record.get(DETAIL_DASAR_GAJI.DASAR_GAJI_ID));
            dg.setDeskripsi(record.get(DASAR_GAJI.DESKRIPSI));
            dg.setTanggalMulai(record.get(DASAR_GAJI.TANGGAL_AWAL));
            dg.setTanggalAkhir(record.get(DASAR_GAJI.TANGGAL_AKHIR));
            dg.setAktif(record.get(DASAR_GAJI.AKTIF));
            response.setDasarGaji(dg);
        }
        return response;
    }
}
