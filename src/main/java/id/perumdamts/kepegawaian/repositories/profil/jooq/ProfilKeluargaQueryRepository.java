package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.mapper.profil.keluarga.ProfilKeluargaJooqMapper;
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
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;

@Repository
@RequiredArgsConstructor
public class ProfilKeluargaQueryRepository {
    private final DSLContext dsl;

    private static final Map<String, Field<?>> ALLOWED_SORTS = Map.of(
            "id", PROFIL_KELUARGA.ID,
            "nama", PROFIL_KELUARGA.NAMA,
            "tanggalLahir", PROFIL_KELUARGA.TANGGAL_LAHIR
    );

    public Page<ProfilKeluargaQuery> pageQuery(ProfilKeluargaIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                ALLOWED_SORTS, PROFIL_KELUARGA.ID);

        var conditions = DSL.trueCondition()
                .and(query.getIsDeleted() != null
                        ? PROFIL_KELUARGA.IS_DELETED.eq(query.getIsDeleted())
                        : PROFIL_KELUARGA.IS_DELETED.eq(false))
                .and(query.getBiodataId() != null
                        ? PROFIL_KELUARGA.BIODATA_ID.eq(query.getBiodataId())
                        : DSL.noCondition())
                .and(query.getHubunganKeluarga() != null
                        ? PROFIL_KELUARGA.HUBUNGAN_KELUARGA.eq(query.getHubunganKeluarga().byteValue())
                        : DSL.noCondition())
                .and(query.getJenisKelamin() != null
                        ? PROFIL_KELUARGA.JENIS_KELAMIN.eq(query.getJenisKelamin().byteValue())
                        : DSL.noCondition());

        var records = dsl.select(ProfilKeluargaSelects.COLUMNS)
                .from(PROFIL_KELUARGA)
                .leftJoin(BIODATA).on(PROFIL_KELUARGA.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(PROFIL_KELUARGA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(conditions)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset((long) query.getPageNumber() * query.getSizeOrDefault())
                .fetch(ProfilKeluargaJooqMapper.INSTANCE);

        Long total = dsl.selectCount()
                .from(PROFIL_KELUARGA)
                .where(conditions)
                .fetchOne(0, Long.class);

        return new PageImpl<>(records, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()),
                total != null ? total : 0L);
    }
}