package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaQuery;
import id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja.PengalamanKerjaJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.PengalamanKerja.PENGALAMAN_KERJA;

@Repository
@RequiredArgsConstructor
public class PengalamanKerjaQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", PENGALAMAN_KERJA.ID,
            "namaPerusahaan", PENGALAMAN_KERJA.NAMA_PERUSAHAAN,
            "typePerusahaan", PENGALAMAN_KERJA.TYPE_PERUSAHAAN,
            "jabatan", PENGALAMAN_KERJA.JABATAN
    );

    public Page<PengalamanKerjaQuery> pageQuery(PengalamanKerjaIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, PENGALAMAN_KERJA.ID);

        var conditions = DSL.trueCondition()
                .and(PENGALAMAN_KERJA.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? PENGALAMAN_KERJA.BIODATA_ID.eq(query.getBiodataId())
                        : DSL.noCondition())
                .and(query.getNamaPerusahaan() != null
                        ? PENGALAMAN_KERJA.NAMA_PERUSAHAAN.contains(query.getNamaPerusahaan())
                        : DSL.noCondition())
                .and(query.getJabatan() != null
                        ? PENGALAMAN_KERJA.JABATAN.contains(query.getJabatan())
                        : DSL.noCondition());

        var count = dsl.selectCount()
                .from(PENGALAMAN_KERJA)
                .where(conditions)
                .fetchOne(0, Long.class);

        var data = dsl.select(PengalamanKerjaSelects.COLUMNS)
                .from(PENGALAMAN_KERJA)
                .leftJoin(BIODATA).on(PENGALAMAN_KERJA.BIODATA_ID.eq(BIODATA.NIK))
                .where(conditions)
                .orderBy(sortOrder)
                .offset(query.getPage() * query.getSize())
                .limit(query.getSize())
                .fetch(PengalamanKerjaJooqMapper.INSTANCE);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }
}
