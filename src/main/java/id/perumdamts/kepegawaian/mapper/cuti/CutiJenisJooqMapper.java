package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;

public final class CutiJenisJooqMapper {
    private CutiJenisJooqMapper() {}

    public static CutiJenisResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiJenisResponse response = new CutiJenisResponse();
        response.setId(record.get(CUTI_JENIS.ID));
        response.setNama(record.get(CUTI_JENIS.NAMA));
        response.setMaxHari(record.get(CUTI_JENIS.MAX_HARI));
        response.setPotongKuotaTahunan(record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN));
        if (record.get("parent_id") != null) {
            CutiJenisMiniResponse mini = new CutiJenisMiniResponse();
            mini.setId((Long) record.get("parent_id"));
            mini.setNama((String) record.get("parent_nama"));
            response.setParent(mini);
        }
        return response;
    }
}
