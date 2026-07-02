package id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.DasarGaji.DASAR_GAJI;

public final class DasarGajiJooqMapper {
    private DasarGajiJooqMapper() {}

    public static DasarGajiResponse mapToResponse(Record record) {
        if (record == null) return null;
        DasarGajiResponse response = new DasarGajiResponse();
        response.setId(record.get(DASAR_GAJI.ID));
        response.setDeskripsi(record.get(DASAR_GAJI.DESKRIPSI));
        response.setTanggalMulai(record.get(DASAR_GAJI.TANGGAL_AWAL));
        response.setTanggalAkhir(record.get(DASAR_GAJI.TANGGAL_AKHIR));
        response.setAktif(record.get(DASAR_GAJI.AKTIF));
        return response;
    }
}
