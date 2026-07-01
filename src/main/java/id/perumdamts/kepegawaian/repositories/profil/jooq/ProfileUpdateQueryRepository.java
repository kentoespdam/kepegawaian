package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateQuery;
import id.perumdamts.kepegawaian.mapper.profil.profilUpdate.ProfileUpdateJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.ProfilUpdate.PROFIL_UPDATE;

@Repository
@RequiredArgsConstructor
public class ProfileUpdateQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", PROFIL_UPDATE.ID,
            "reqDate", PROFIL_UPDATE.REQ_DATE,
            "approvalDate", PROFIL_UPDATE.APPROVAL_DATE,
            "nipam", PROFIL_UPDATE.NIPAM,
            "nama", PROFIL_UPDATE.NAMA
    );

    public Page<ProfileUpdateQuery> pageQuery(ProfileUpdateRequest query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, PROFIL_UPDATE.REQ_DATE);

        var conditions = DSL.trueCondition()
                .and(query.getNipam() != null
                        ? PROFIL_UPDATE.NIPAM.containsIgnoreCase(query.getNipam())
                        : DSL.noCondition())
                .and(query.getNama() != null
                        ? PROFIL_UPDATE.NAMA.containsIgnoreCase(query.getNama())
                        : DSL.noCondition())
                .and(query.getApprovalStatus() != null
                        ? PROFIL_UPDATE.APPROVAL_STATUS.eq((byte) query.getApprovalStatus().ordinal())
                        : DSL.noCondition())
                .and(query.getTanggalPengajuan() != null
                        ? DSL.field("DATE({0})", java.time.LocalDate.class, PROFIL_UPDATE.REQ_DATE).eq(query.getTanggalPengajuan())
                        : DSL.noCondition());

        var total = dsl.selectCount()
                .from(PROFIL_UPDATE)
                .where(conditions)
                .fetchOneInto(Long.class);

        var rows = dsl.select(
                        PROFIL_UPDATE.ID,
                        PROFIL_UPDATE.NIPAM,
                        PROFIL_UPDATE.NAMA,
                        PROFIL_UPDATE.JABATAN,
                        PROFIL_UPDATE.REQ_DATE,
                        PROFIL_UPDATE.TABLE_NAME,
                        PROFIL_UPDATE.ACTION_TYPE,
                        PROFIL_UPDATE.DATA_DESCRIPTION,
                        PROFIL_UPDATE.REV_ID,
                        PROFIL_UPDATE.APPROVAL_STATUS,
                        PROFIL_UPDATE.APPROVAL_DATE,
                        PROFIL_UPDATE.APPROVAL_PIC
                )
                .from(PROFIL_UPDATE)
                .where(conditions)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(ProfileUpdateJooqMapper.INSTANCE);

        return new PageImpl<>(rows, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()),
                total != null ? total : 0L);
    }
}
