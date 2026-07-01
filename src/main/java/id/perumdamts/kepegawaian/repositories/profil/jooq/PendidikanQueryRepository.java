package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import id.perumdamts.kepegawaian.mapper.profil.pendidikan.PendidikanJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class PendidikanQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", PENDIDIKAN.ID,
            "jenjangId", PENDIDIKAN.JENJANG_ID,
            "institusi", PENDIDIKAN.INSTITUSI,
            "tahunMasuk", PENDIDIKAN.TAHUN_MASUK,
            "tahunLulus", PENDIDIKAN.TAHUN_LULUS
    );

    public Page<PendidikanQuery> pageQuery(PendidikanIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, PENDIDIKAN.ID);

        var conditions = DSL.trueCondition()
                .and(PENDIDIKAN.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? PENDIDIKAN.BIODATA_ID.eq(query.getBiodataId())
                        : DSL.noCondition())
                .and(query.getJenjangId() != null
                        ? PENDIDIKAN.JENJANG_ID.eq(query.getJenjangId())
                        : DSL.noCondition())
                .and(query.getIsLatest() != null
                        ? PENDIDIKAN.IS_LATEST.eq((byte) (query.getIsLatest() ? 1 : 0))
                        : DSL.noCondition());

        var count = dsl.selectCount()
                .from(PENDIDIKAN)
                .where(conditions)
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(PendidikanSelects.COLUMNS)
                .from(PENDIDIKAN)
                .leftJoin(BIODATA).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .offset(query.getPage() * query.getSize())
                .limit(query.getSize())
                .fetch(PendidikanJooqMapper.INSTANCE);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }
}
