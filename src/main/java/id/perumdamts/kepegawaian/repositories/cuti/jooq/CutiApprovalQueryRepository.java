package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.mapper.cuti.CutiApprovalJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.CutiApproval.CUTI_APPROVAL;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class CutiApprovalQueryRepository {
    private final DSLContext dsl;

    public Page<CutiApprovalMiniResponse> pageQuery(Long cutiId, CutiApprovalRequest query) {
        Condition where = baseWhere(cutiId, query);

        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_APPROVAL.ID);

        var count = dsl.selectCount().from(CUTI_APPROVAL)
                .where(where).fetchOne(0, Long.class);

        int pageNumber = query.getPage() != null ? query.getPage() : 0;
        int sizeOrDefault = query.getSize() != null ? query.getSize() : 10;

        var data = dsl.select(
                        CUTI_APPROVAL.ID,
                        CUTI_APPROVAL.APPROVAL_LEVEL,
                        CUTI_APPROVAL.APPROVAL_STATUS,
                        CUTI_APPROVAL.NOTES,
                        CUTI_APPROVAL.CREATED_AT,
                        PEGAWAI.ID.as("approver_id"),
                        PEGAWAI.NIPAM.as("approver_nipam"),
                        BIODATA.NAMA.as("approver_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.KODE.as("jab_kode"),
                        JABATAN.NAMA.as("jab_nama")
                )
                .from(CUTI_APPROVAL)
                .leftJoin(PEGAWAI).on(CUTI_APPROVAL.APPROVER_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(CUTI_APPROVAL.JABATAN_ID.eq(JABATAN.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(sizeOrDefault)
                .offset(pageNumber * sizeOrDefault)
                .fetch(CutiApprovalJooqMapper::mapToResponse);

        return new PageImpl<>(data, PageRequest.of(pageNumber, sizeOrDefault), count != null ? count : 0);
    }

    private Condition baseWhere(Long cutiId, CutiApprovalRequest query) {
        Condition where = DSL.noCondition();
        if (cutiId != null) {
            where = where.and(CUTI_APPROVAL.CUTI_PEGAWAI_ID.eq(cutiId));
        }
        if (query.getId() != null) {
            where = where.and(CUTI_APPROVAL.ID.eq(query.getId()));
        }
        if (query.getApproverId() != null) {
            where = where.and(CUTI_APPROVAL.APPROVER_ID.eq(query.getApproverId()));
        }
        if (query.getJabatanId() != null) {
            where = where.and(CUTI_APPROVAL.JABATAN_ID.eq(query.getJabatanId()));
        }
        return where;
    }

    private Map<String, org.jooq.Field<?>> allowedSorts() {
        return Map.of(
                "id", CUTI_APPROVAL.ID,
                "createdAt", CUTI_APPROVAL.CREATED_AT,
                "approvalLevel", CUTI_APPROVAL.APPROVAL_LEVEL
        );
    }
}
