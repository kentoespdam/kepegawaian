package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.profesi.*;
import id.perumdamts.kepegawaian.mapper.master.profesi.ProfesiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.AlatKerja.ALAT_KERJA;
import static id.perumdamts.kepegawaian.jooq.tables.Apd.APD;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class ProfesiQueryRepository {
    private final DSLContext dsl;

    public Page<ProfesiDetail> pageQuery(ProfesiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), PROFESI.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount().from(PROFESI).where(where).fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(ProfesiSelects.PROFESI_COLUMNS)
                .select(
                        multiset(dsl.select(APD.ID, APD.NAMA)
                                .from(APD)
                                .where(APD.PROFESI_ID.eq(PROFESI.ID))
                                .and(APD.IS_DELETED.eq(false))
                                .orderBy(APD.NAMA.asc()))
                                .as("apd_list")
                                .convertFrom(r -> r.map(mapping(ApdRow::new))),
                        multiset(dsl.select(ALAT_KERJA.ID, ALAT_KERJA.NAMA)
                                .from(ALAT_KERJA)
                                .where(ALAT_KERJA.PROFESI_ID.eq(PROFESI.ID))
                                .and(ALAT_KERJA.IS_DELETED.eq(false))
                                .orderBy(ALAT_KERJA.NAMA.asc()))
                                .as("alat_kerja_list")
                                .convertFrom(r -> r.map(mapping(AlatKerjaRow::new))))
                .from(PROFESI)
                .leftJoin(ORGANISASI).on(PROFESI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PROFESI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(PROFESI.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GRADE).on(PROFESI.GRADE_ID.eq(GRADE.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(ProfesiJooqMapper::toDetail);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", PROFESI.NAMA,
                "jabatanId", PROFESI.JABATAN_ID,
                "levelId", PROFESI.LEVEL_ID,
                "gradeId", PROFESI.GRADE_ID
        );
    }

    public List<ProfesiListResponse> listQuery() {
        return dsl.select(PROFESI.ID, PROFESI.NAMA)
                .from(PROFESI)
                .where(PROFESI.IS_DELETED.eq(false))
                .orderBy(PROFESI.NAMA.asc())
                .fetchInto(ProfesiListResponse.class);
    }

    private Condition baseWhere(ProfesiIndexQuery q) {
        return PROFESI.IS_DELETED.eq(false)
                .and(q.getOrganisasiId() != null ? PROFESI.ORGANISASI_ID.eq(q.getOrganisasiId()) : DSL.noCondition())
                .and(q.getJabatanId() != null ? PROFESI.JABATAN_ID.eq(q.getJabatanId()) : DSL.noCondition())
                .and(q.getLevelId() != null ? PROFESI.LEVEL_ID.eq(q.getLevelId()) : DSL.noCondition())
                .and(q.getGradeId() != null ? PROFESI.GRADE_ID.eq(q.getGradeId()) : DSL.noCondition())
                .and(q.getNama() != null ? PROFESI.NAMA.likeIgnoreCase("%" + q.getNama() + "%") : DSL.noCondition());
    }
}
