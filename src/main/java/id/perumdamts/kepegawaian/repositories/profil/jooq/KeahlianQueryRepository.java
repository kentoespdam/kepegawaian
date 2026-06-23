package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.jooq.tables.Biodata;
import id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Keahlian.KEAHLIAN;

@Repository
@RequiredArgsConstructor
public class KeahlianQueryRepository {

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", KEAHLIAN.ID,
            "tahun", KEAHLIAN.TAHUN,
            "institusi", KEAHLIAN.INSTITUSI
    );

    private final DSLContext dsl;

    public Page<KeahlianQuery> pageQuery(KeahlianIndexQuery query) {
        var sort = SortParam.resolve(
                query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, KEAHLIAN.ID);

        var conditions = org.jooq.impl.DSL.trueCondition()
                .and(KEAHLIAN.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? KEAHLIAN.BIODATA_ID.eq(query.getBiodataId())
                        : org.jooq.impl.DSL.noCondition())
                .and(query.getJenisKeahlianId() != null
                        ? KEAHLIAN.JENIS_KEAHLIAN_ID.eq(query.getJenisKeahlianId())
                        : org.jooq.impl.DSL.noCondition())
                ;

        long count = dsl.selectCount()
                .from(KEAHLIAN)
                .where(conditions)
                .fetchOne(0, Long.class);

        List<KeahlianQuery> data = dsl.select(KeahlianSelects.COLUMNS)
                .from(KEAHLIAN)
                .leftJoin(Biodata.BIODATA)
                .on(KEAHLIAN.BIODATA_ID.eq(Biodata.BIODATA.NIK))
                .leftJoin(JenisKeahlian.JENIS_KEAHLIAN)
                .on(KEAHLIAN.JENIS_KEAHLIAN_ID.eq(JenisKeahlian.JENIS_KEAHLIAN.ID))
                .where(conditions)
                .orderBy(sort)
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .limit(query.getSizeOrDefault())
                .fetch(new KeahlianRowMapper());

        return new PageImpl<>(data,
                PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()),
                count);
    }
}
