package id.perumdamts.kepegawaian.mapper.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

public final class SanksiJooqMapper {
    private SanksiJooqMapper() {}

    public static SanksiQuery mapToQuery(Record record) {
        SanksiQuery query = new SanksiQuery();
        query.setId(record.get(SANKSI_SP.ID));
        query.setKode(record.get(SANKSI_SP.KODE));
        query.setKeterangan(record.get(SANKSI_SP.KETERANGAN));
        query.setJenisSpId(record.get(SANKSI_SP.JENIS_SP_ID));
        query.setPotTkk(record.get(SANKSI_SP.POT_TKK));
        query.setJmlPotTkk(record.get(SANKSI_SP.JML_POT_TKK));
        query.setIsPendingPangkat(record.get(SANKSI_SP.IS_PENDING_PANGKAT));
        query.setIsPendingGaji(record.get(SANKSI_SP.IS_PENDING_GAJI));
        query.setIsTurunPangkat(record.get(SANKSI_SP.IS_TURUN_PANGKAT));
        query.setIsTurunJabatan(record.get(SANKSI_SP.IS_TURUN_JABATAN));
        query.setIsSuspension(record.get(SANKSI_SP.IS_SUSPENSION));
        query.setIsTerminateDh(record.get(SANKSI_SP.IS_TERMINATE_DH));
        query.setIsTerminateTh(record.get(SANKSI_SP.IS_TERMINATE_TH));

        Long jenisSpId = record.get(JENIS_SP.ID.as("jenissp_id"));
        if (jenisSpId != null) {
            JenisSpMiniResponse j = new JenisSpMiniResponse();
            j.setId(jenisSpId);
            j.setKode(record.get(JENIS_SP.KODE.as("jenissp_kode")));
            j.setNama(record.get(JENIS_SP.NAMA.as("jenissp_nama")));
            query.setJenisSp(j);
        }
        return query;
    }
}
