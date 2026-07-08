package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiApproval.CUTI_APPROVAL;

public final class CutiApprovalJooqMapper {
    private CutiApprovalJooqMapper() {}

    public static CutiApprovalMiniResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiApprovalMiniResponse res = new CutiApprovalMiniResponse();
        res.setId(record.get(CUTI_APPROVAL.ID));
        res.setApprovalLevel(record.get(CUTI_APPROVAL.APPROVAL_LEVEL));
        res.setApprovalStatus(toApprovalCutiStatus(record.get(CUTI_APPROVAL.APPROVAL_STATUS)));
        res.setNotes(record.get(CUTI_APPROVAL.NOTES));
        res.setCreatedAt(record.get(CUTI_APPROVAL.CREATED_AT));

        if (record.get("approver_id") != null) {
            PegawaiMiniResponse app = new PegawaiMiniResponse();
            app.setId((Long) record.get("approver_id"));
            app.setNipam((String) record.get("approver_nipam"));
            app.setNama((String) record.get("approver_nama"));
            res.setApprover(app);
        }
        if (record.get("jab_id") != null) {
            res.setJabatan(new JabatanMiniResponse(
                    (Long) record.get("jab_id"),
                    (String) record.get("jab_kode"),
                    null,
                    (String) record.get("jab_nama")));
        }
        return res;
    }

    private static EApprovalCutiStatus toApprovalCutiStatus(Byte val) {
        if (val == null) return null;
        int intVal = val.intValue();
        if (intVal >= 0 && intVal < EApprovalCutiStatus.values().length) {
            return EApprovalCutiStatus.values()[intVal];
        }
        return null;
    }
}
