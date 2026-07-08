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
        PegawaiMiniResponse approver = record.get("approver_id") != null
                ? new PegawaiMiniResponse(
                (Long) record.get("approver_id"),
                (String) record.get("approver_nipam"),
                (String) record.get("approver_nama"),
                null, null, null)
                : null;
        JabatanMiniResponse jabatan = record.get("jab_id") != null
                ? new JabatanMiniResponse(
                (Long) record.get("jab_id"),
                (String) record.get("jab_kode"),
                null,
                (String) record.get("jab_nama"))
                : null;
        return new CutiApprovalMiniResponse(
                record.get(CUTI_APPROVAL.ID),
                approver,
                jabatan,
                record.get(CUTI_APPROVAL.APPROVAL_LEVEL),
                toApprovalCutiStatus(record.get(CUTI_APPROVAL.APPROVAL_STATUS)),
                record.get(CUTI_APPROVAL.NOTES),
                record.get(CUTI_APPROVAL.CREATED_AT)
        );
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
