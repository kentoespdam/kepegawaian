package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataQuery;
import id.perumdamts.kepegawaian.mapper.profil.biodata.BiodataJooqMapper;
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
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class BiodataQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "nik", BIODATA.NIK,
            "nama", BIODATA.NAMA,
            "jenisKelamin", BIODATA.JENIS_KELAMIN,
            "alamat", BIODATA.ALAMAT,
            "createdAt", BIODATA.CREATED_AT
    );

    public Page<BiodataQuery> pageQuery(BiodataIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, BIODATA.NAMA);

        var conditions = DSL.trueCondition()
                .and(BIODATA.IS_DELETED.eq(false))
                .and(query.getNik() != null
                        ? BIODATA.NIK.containsIgnoreCase(query.getNik())
                        : DSL.noCondition())
                .and(query.getNama() != null
                        ? BIODATA.NAMA.containsIgnoreCase(query.getNama())
                        : DSL.noCondition())
                .and(query.getJenisKelamin() != null
                        ? BIODATA.JENIS_KELAMIN.eq((byte) query.getJenisKelamin().ordinal())
                        : DSL.noCondition())
                .and(query.getAlamat() != null
                        ? BIODATA.ALAMAT.containsIgnoreCase(query.getAlamat())
                        : DSL.noCondition())
                .and(query.getIsPegawai() != null
                        ? BIODATA.IS_PEGAWAI.eq(query.getIsPegawai())
                        : DSL.noCondition());

        var total = dsl.selectCount()
                .from(BIODATA)
                .leftJoin(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(conditions)
                .fetchOneInto(Long.class);

        var rows = dsl.select(BiodataSelects.COLUMNS)
                .from(BIODATA)
                .leftJoin(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(BiodataJooqMapper.INSTANCE);

        return new PageImpl<>(rows, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()),
                total != null ? total : 0L);
    }

    public List<BiodataQuery> listQuery() {
        var conditions = DSL.trueCondition()
                .and(BIODATA.IS_DELETED.eq(false));

        return dsl.select(BiodataSelects.COLUMNS)
                .from(BIODATA)
                .leftJoin(JENJANG_PENDIDIKAN).on(BIODATA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(conditions)
                .fetch(BiodataJooqMapper.INSTANCE);
    }
}
