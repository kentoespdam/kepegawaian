package id.perumdamts.kepegawaian.mapper.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateQuery;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.data.history.RevisionMetadata;

import static id.perumdamts.kepegawaian.jooq.tables.ProfilUpdate.PROFIL_UPDATE;

public final class ProfileUpdateJooqMapper implements RecordMapper<Record, ProfileUpdateQuery> {
    public static final ProfileUpdateJooqMapper INSTANCE = new ProfileUpdateJooqMapper();

    private ProfileUpdateJooqMapper() {}

    @Override
    public ProfileUpdateQuery map(Record record) {
        ProfileUpdateQuery dto = new ProfileUpdateQuery();
        dto.setId(record.get(PROFIL_UPDATE.ID));
        dto.setNipam(record.get(PROFIL_UPDATE.NIPAM));
        dto.setNama(record.get(PROFIL_UPDATE.NAMA));
        dto.setJabatan(record.get(PROFIL_UPDATE.JABATAN));
        dto.setReqDate(record.get(PROFIL_UPDATE.REQ_DATE));

        Byte tableNameByte = record.get(PROFIL_UPDATE.TABLE_NAME);
        if (tableNameByte != null) {
            dto.setTableName(EProfileUpdateTable.values()[tableNameByte]);
        }

        Byte actionTypeByte = record.get(PROFIL_UPDATE.ACTION_TYPE);
        if (actionTypeByte != null) {
            dto.setActionType(RevisionMetadata.RevisionType.values()[actionTypeByte]);
        }

        dto.setDataDescription(record.get(PROFIL_UPDATE.DATA_DESCRIPTION));
        dto.setRevId(record.get(PROFIL_UPDATE.REV_ID));

        Byte approvalStatusByte = record.get(PROFIL_UPDATE.APPROVAL_STATUS);
        if (approvalStatusByte != null) {
            dto.setApprovalStatus(EProfileUpdateApproval.values()[approvalStatusByte]);
        }

        dto.setApprovalDate(record.get(PROFIL_UPDATE.APPROVAL_DATE));
        dto.setApprovalPic(record.get(PROFIL_UPDATE.APPROVAL_PIC));
        return dto;
    }
}
