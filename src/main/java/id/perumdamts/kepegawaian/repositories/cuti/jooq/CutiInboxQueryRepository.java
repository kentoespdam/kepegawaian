package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.CutiApprovalChain.CUTI_APPROVAL_CHAIN;
import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;

@Repository
@RequiredArgsConstructor
public class CutiInboxQueryRepository {
    private final DSLContext dsl;
    private final CutiPengajuanQueryRepository pengajuanQueryRepository;
    public Page<CutiApprovalChainResponse> pageQuery(CutiApprovalChainRequest query) {
        Condition where = baseWhere(query);
        
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_APPROVAL_CHAIN.ID);
                
        var count = dsl.select(DSL.countDistinct(CUTI_APPROVAL_CHAIN.REF_CUTI_ID))
                .from(CUTI_APPROVAL_CHAIN)
                .leftJoin(CUTI_PEGAWAI).on(CUTI_APPROVAL_CHAIN.REF_CUTI_ID.eq(CUTI_PEGAWAI.ID))
                .where(where).fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(
                        DSL.max(CUTI_APPROVAL_CHAIN.ID).as("id"),
                        DSL.max(CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL).as("approval_level"),
                        DSL.max(CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS).as("read_write_status"),
                        CUTI_APPROVAL_CHAIN.REF_CUTI_ID
                )
                .from(CUTI_APPROVAL_CHAIN)
                .leftJoin(CUTI_PEGAWAI).on(CUTI_APPROVAL_CHAIN.REF_CUTI_ID.eq(CUTI_PEGAWAI.ID))
                .where(where)
                .groupBy(CUTI_APPROVAL_CHAIN.REF_CUTI_ID)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(record -> {
                    Long id = record.get("id", Long.class);
                    Integer approvalLevel = record.get("approval_level", Integer.class);
                    
                    Byte rwVal = record.get("read_write_status", Byte.class);
                    EReadWriteStatus readWriteStatus = (rwVal != null && rwVal >= 0 && rwVal < EReadWriteStatus.values().length)
                            ? EReadWriteStatus.values()[rwVal] : null;
                    
                    Long refCutiId = record.get(CUTI_APPROVAL_CHAIN.REF_CUTI_ID);
                    CutiPengajuanResponse refCuti = refCutiId != null ? pengajuanQueryRepository.getById(refCutiId) : null;
                    
                    return new CutiApprovalChainResponse(
                            id, approvalLevel, readWriteStatus, refCuti
                    );
                });
                
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", CUTI_APPROVAL_CHAIN.ID,
                "approvalLevel", CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL,
                "readWriteStatus", CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS
        );
    }

    private Condition baseWhere(CutiApprovalChainRequest q) {
        Condition cond = CUTI_PEGAWAI.IS_DELETED.eq(false);
        
        // Either match picSaatIniId or specific jabatanId
        if (q.getJabatanId() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.JABATAN_ID.eq(q.getJabatanId()));
        } else if (q.getPicSaatIniId() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.JABATAN_ID.eq(q.getPicSaatIniId()));
        }
        
        if (q.getApprovalCutiStatus() != null) {
            cond = cond.and(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS.eq((byte) q.getApprovalCutiStatus().ordinal()));
        }
        
        if (q.getReadWriteStatus() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS.eq((byte) q.getReadWriteStatus().ordinal()));
        }
        
        if (q.getTahun() != null) {
            cond = cond.and(
                DSL.year(CUTI_PEGAWAI.CREATED_AT).eq(q.getTahun())
                .or(DSL.year(CUTI_PEGAWAI.TANGGAL_MULAI).eq(q.getTahun()))
            );
        }
        
        return cond;
    }
}
