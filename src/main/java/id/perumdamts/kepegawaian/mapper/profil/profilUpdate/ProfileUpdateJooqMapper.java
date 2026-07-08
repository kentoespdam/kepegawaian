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
        Byte tableNameByte = record.get(PROFIL_UPDATE.TABLE_NAME);
        EProfileUpdateTable tableName = tableNameByte != null ? EProfileUpdateTable.values()[tableNameByte] : null;

        Byte actionTypeByte = record.get(PROFIL_UPDATE.ACTION_TYPE);
        RevisionMetadata.RevisionType actionType = actionTypeByte != null ? RevisionMetadata.RevisionType.values()[actionTypeByte] : null;

        Byte approvalStatusByte = record.get(PROFIL_UPDATE.APPROVAL_STATUS);
        EProfileUpdateApproval approvalStatus = approvalStatusByte != null ? EProfileUpdateApproval.values()[approvalStatusByte] : null;

        return new ProfileUpdateQuery(
                record.get(PROFIL_UPDATE.ID),
                record.get(PROFIL_UPDATE.NIPAM),
                record.get(PROFIL_UPDATE.NAMA),
                record.get(PROFIL_UPDATE.JABATAN),
                record.get(PROFIL_UPDATE.REQ_DATE),
                tableName,
                actionType,
                record.get(PROFIL_UPDATE.DATA_DESCRIPTION),
                record.get(PROFIL_UPDATE.REV_ID),
                approvalStatus,
                record.get(PROFIL_UPDATE.APPROVAL_DATE),
                record.get(PROFIL_UPDATE.APPROVAL_PIC)
        );
    }
}
