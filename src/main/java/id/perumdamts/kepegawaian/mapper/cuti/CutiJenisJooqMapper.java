package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;

public final class CutiJenisJooqMapper {
    private CutiJenisJooqMapper() {}

    public static CutiJenisResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiJenisMiniResponse parent = record.get("parent_id") != null
                ? new CutiJenisMiniResponse((Long) record.get("parent_id"), (String) record.get("parent_nama"), null)
                : null;
        return new CutiJenisResponse(
                record.get(CUTI_JENIS.ID),
                parent,
                record.get(CUTI_JENIS.NAMA),
                record.get(CUTI_JENIS.MAX_HARI),
                record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN)
        );
    }

    public static CutiJenisMiniResponse mapToMini(Record record) {
        if (record == null) return null;
        return new CutiJenisMiniResponse(
                record.get(CUTI_JENIS.ID),
                record.get(CUTI_JENIS.NAMA),
                record.get(CUTI_JENIS.PARENT_ID)
        );
    }
}
