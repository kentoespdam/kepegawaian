package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanQuery;
import id.perumdamts.kepegawaian.mapper.profil.pelatihan.PelatihanJooqMapper;
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
import static id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan.JENIS_PELATIHAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;

@Repository
@RequiredArgsConstructor
public class PelatihanQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", PELATIHAN.ID,
            "jenisPelatihanId", PELATIHAN.JENIS_PELATIHAN_ID,
            "nama", PELATIHAN.NAMA,
            "lembaga", PELATIHAN.LEMBAGA
    );

    public Page<PelatihanQuery> pageQuery(PelatihanIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, PELATIHAN.ID);

        var conditions = DSL.trueCondition()
                .and(PELATIHAN.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? PELATIHAN.BIODATA_ID.eq(query.getBiodataId())
                        : DSL.noCondition())
                .and(query.getJenisPelatihanId() != null
                        ? PELATIHAN.JENIS_PELATIHAN_ID.eq(query.getJenisPelatihanId())
                        : DSL.noCondition())
                .and(query.getNama() != null
                        ? PELATIHAN.NAMA.containsIgnoreCase(query.getNama())
                        : DSL.noCondition())
                .and(query.getLembaga() != null
                        ? PELATIHAN.LEMBAGA.containsIgnoreCase(query.getLembaga())
                        : DSL.noCondition());

        var count = dsl.selectCount()
                .from(PELATIHAN)
                .where(conditions)
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(PelatihanSelects.COLUMNS)
                .from(PELATIHAN)
                .leftJoin(BIODATA).on(PELATIHAN.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENIS_PELATIHAN).on(PELATIHAN.JENIS_PELATIHAN_ID.eq(JENIS_PELATIHAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .offset(query.getPage() * query.getSize())
                .limit(query.getSize())
                .fetch(PelatihanJooqMapper.INSTANCE);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }
}