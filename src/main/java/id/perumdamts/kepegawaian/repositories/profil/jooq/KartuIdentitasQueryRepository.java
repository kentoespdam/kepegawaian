package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas.KartuIdentitasJooqMapper;
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
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;

@Repository
@RequiredArgsConstructor
public class KartuIdentitasQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", KARTU_IDENTITAS.ID,
            "jenisKartuId", KARTU_IDENTITAS.JENIS_KITAS_ID,
            "nomorKartu", KARTU_IDENTITAS.NOMOR_KARTU
    );

    public Page<KartuIdentitasQuery> pageQuery(KartuIdentitasIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, KARTU_IDENTITAS.ID);

        var conditions = DSL.trueCondition()
                .and(KARTU_IDENTITAS.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? KARTU_IDENTITAS.NIK.eq(query.getBiodataId())
                        : DSL.noCondition())
                .and(query.getJenisKartuId() != null
                        ? KARTU_IDENTITAS.JENIS_KITAS_ID.eq(query.getJenisKartuId())
                        : DSL.noCondition())
                .and(query.getNomorKartu() != null
                        ? KARTU_IDENTITAS.NOMOR_KARTU.eq(query.getNomorKartu())
                        : DSL.noCondition());

        var count = dsl.selectCount()
                .from(KARTU_IDENTITAS)
                .where(conditions)
                .fetchOne(0, Long.class);

        var data = dsl.select(KartuIdentitasSelects.COLUMNS)
                .from(KARTU_IDENTITAS)
                .leftJoin(BIODATA).on(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .offset(query.getPage() * query.getSize())
                .limit(query.getSize())
                .fetch(KartuIdentitasJooqMapper.INSTANCE);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }
}
