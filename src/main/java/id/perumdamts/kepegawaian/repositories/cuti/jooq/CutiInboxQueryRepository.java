package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.mapper.cuti.CutiPegawaiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.CutiApprovalChain.CUTI_APPROVAL_CHAIN;
import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;
import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class CutiInboxQueryRepository {
    private final DSLContext dsl;

    public Page<CutiApprovalChainResponse> pageQuery(CutiApprovalChainRequest query) {
        // Chain filters (jabatan/readWrite) MUST live inside the ranked subquery: the outer
        // query only sees the derived table alias, and per decisions-cuti.md the jabatan
        // filter applies BEFORE ROW_NUMBER so the representative row is the highest approval
        // level among the viewer's own chain rows. Pegawai filters stay on the outer join.
        var chainWhere = chainWhere(query);
        var pegawaiWhere = pegawaiWhere(query);

        var count = dsl.select(DSL.countDistinct(CUTI_APPROVAL_CHAIN.REF_CUTI_ID))
                .from(CUTI_APPROVAL_CHAIN)
                .leftJoin(CUTI_PEGAWAI).on(CUTI_APPROVAL_CHAIN.REF_CUTI_ID.eq(CUTI_PEGAWAI.ID))
                .where(chainWhere)
                .and(pegawaiWhere)
                .fetchOptional(0, Long.class).orElse(0L);

        // FIX N+1: Use ROW_NUMBER() subquery instead of calling getById() per row
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");

        var ranked = dsl.select(
                        CUTI_APPROVAL_CHAIN.ID,
                        CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL,
                        CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS,
                        CUTI_APPROVAL_CHAIN.REF_CUTI_ID,
                        DSL.rowNumber().over()
                                .partitionBy(CUTI_APPROVAL_CHAIN.REF_CUTI_ID)
                                .orderBy(CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL.desc(), CUTI_APPROVAL_CHAIN.ID.desc())
                                .as("rn")
                ).from(CUTI_APPROVAL_CHAIN)
                .where(chainWhere)
                .asTable("ranked");

        // Sort columns must be qualified to the derived table (`ranked`.`id`, ...) —
        // referencing CUTI_APPROVAL_CHAIN here would render an out-of-scope table name.
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(), Map.of(
                        "id", ranked.field(CUTI_APPROVAL_CHAIN.ID),
                        "approvalLevel", ranked.field(CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL),
                        "readWriteStatus", ranked.field(CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS)
                ), ranked.field(CUTI_APPROVAL_CHAIN.ID));

        Field<?>[] allFields = Stream.concat(
                        Stream.of(
                                ranked.field(CUTI_APPROVAL_CHAIN.ID).as("chain_id"),
                                ranked.field(CUTI_APPROVAL_CHAIN.APPROVAL_LEVEL).as("chain_approval_level"),
                                ranked.field(CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS).as("chain_read_write_status")
                        ),
                        Arrays.stream(CutiPegawaiSelects.miniQueryFields(jenisCuti, subJenisCuti, pic))
                ).toArray(Field<?>[]::new);

        var data = dsl.select(allFields)
                .from(ranked)
                .leftJoin(CUTI_PEGAWAI).on(ranked.field(CUTI_APPROVAL_CHAIN.REF_CUTI_ID).eq(CUTI_PEGAWAI.ID))
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(DSL.field(DSL.name("ranked", "rn")).eq(1))
                .and(pegawaiWhere)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(record -> {
                    Long id = record.get("chain_id", Long.class);
                    Integer approvalLevel = record.get("chain_approval_level", Integer.class);
                    Byte rwVal = record.get("chain_read_write_status", Byte.class);
                    EReadWriteStatus readWriteStatus = (rwVal != null && rwVal >= 0
                            && rwVal < EReadWriteStatus.values().length)
                            ? EReadWriteStatus.values()[rwVal] : null;
                    CutiPengajuanMiniResponse refCuti = CutiPegawaiJooqMapper.mapToMiniResponse(record);
                    return new CutiApprovalChainResponse(id, approvalLevel, readWriteStatus, refCuti);
                });

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    /** Conditions on {@code cuti_approval_chain} — applied INSIDE the ranked subquery. */
    private static Condition chainWhere(CutiApprovalChainRequest q) {
        Condition cond = DSL.trueCondition();

        // Either match picSaatIniId or specific jabatanId
        if (q.getJabatanId() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.JABATAN_ID.eq(q.getJabatanId()));
        } else if (q.getPicSaatIniId() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.JABATAN_ID.eq(q.getPicSaatIniId()));
        }

        if (q.getReadWriteStatus() != null) {
            cond = cond.and(CUTI_APPROVAL_CHAIN.READ_WRITE_STATUS.eq((byte) q.getReadWriteStatus().ordinal()));
        }

        return cond;
    }

    /** Conditions on {@code cuti_pegawai} — applied on the outer query (joined tables). */
    private static Condition pegawaiWhere(CutiApprovalChainRequest q) {
        Condition cond = CUTI_PEGAWAI.IS_DELETED.eq(false);

        if (q.getApprovalCutiStatus() != null) {
            cond = cond.and(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS.eq((byte) q.getApprovalCutiStatus().ordinal()));
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
