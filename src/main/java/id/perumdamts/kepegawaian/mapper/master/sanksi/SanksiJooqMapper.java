package id.perumdamts.kepegawaian.mapper.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpSimple;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiJenisSpList;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import id.perumdamts.kepegawaian.repositories.master.jooq.SanksiSelects;
import org.jooq.Record;

public final class SanksiJooqMapper {
    private SanksiJooqMapper() {}

    public static SanksiQuery toQuery(Record record) {
        JenisSpMiniResponse j = null;
        Long jenisSpId = record.get(SanksiSelects.JENIS_SP_ID);
        if (jenisSpId != null) {
            j = new JenisSpMiniResponse(
                    jenisSpId,
                    record.get(SanksiSelects.JENIS_SP_KODE),
                    record.get(SanksiSelects.JENIS_SP_NAMA),
                    null);
        }
        return new SanksiQuery(
                record.get(SanksiSelects.ID),
                record.get(SanksiSelects.KODE),
                record.get(SanksiSelects.KETERANGAN),
                j,
                record.get(SanksiSelects.POT_TKK),
                record.get(SanksiSelects.JML_POT_TKK),
                record.get(SanksiSelects.IS_PENDING_PANGKAT),
                record.get(SanksiSelects.IS_PENDING_GAJI),
                record.get(SanksiSelects.IS_TURUN_PANGKAT),
                record.get(SanksiSelects.IS_TURUN_JABATAN),
                record.get(SanksiSelects.IS_SUSPENSION),
                record.get(SanksiSelects.IS_TERMINATE_DH),
                record.get(SanksiSelects.IS_TERMINATE_TH)
        );
    }

    /**
     * Mapping untuk endpoint /master/sanksi/jenis-sp/{id} — nested
     * {@link JenisSpSimple} tanpa circular reference {@code sanksiSp}.
     */
    public static SanksiJenisSpList toJenisSpList(Record record) {
        JenisSpSimple j = null;
        Long jenisSpId = record.get(SanksiSelects.JENIS_SP_ID);
        if (jenisSpId != null) {
            j = new JenisSpSimple(
                    jenisSpId,
                    record.get(SanksiSelects.JENIS_SP_KODE),
                    record.get(SanksiSelects.JENIS_SP_NAMA));
        }
        return new SanksiJenisSpList(
                record.get(SanksiSelects.ID),
                record.get(SanksiSelects.KODE),
                record.get(SanksiSelects.KETERANGAN),
                j,
                record.get(SanksiSelects.POT_TKK),
                record.get(SanksiSelects.JML_POT_TKK),
                record.get(SanksiSelects.IS_PENDING_PANGKAT),
                record.get(SanksiSelects.IS_PENDING_GAJI),
                record.get(SanksiSelects.IS_TURUN_PANGKAT),
                record.get(SanksiSelects.IS_TURUN_JABATAN),
                record.get(SanksiSelects.IS_SUSPENSION),
                record.get(SanksiSelects.IS_TERMINATE_DH),
                record.get(SanksiSelects.IS_TERMINATE_TH)
        );
    }
}
