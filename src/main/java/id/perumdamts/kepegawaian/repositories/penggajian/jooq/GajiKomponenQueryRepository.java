package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenMiniProjection;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiKomponen.GajiKomponenJooqMapper;
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
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.GajiKomponen.GAJI_KOMPONEN;
import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;

@Repository
@RequiredArgsConstructor
public class GajiKomponenQueryRepository {
    private final DSLContext dsl;

    public Page<GajiKomponenResponse> pageQuery(Long profilId, GajiKomponenIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_KOMPONEN.URUT);
        Condition where = baseWhere(profilId, query);
        var count = dsl.selectCount()
                .from(GAJI_KOMPONEN)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_KOMPONEN.ID,
                        GAJI_KOMPONEN.URUT,
                        GAJI_KOMPONEN.PROFIL_GAJI_ID,
                        GAJI_KOMPONEN.KODE,
                        GAJI_KOMPONEN.NAMA,
                        GAJI_KOMPONEN.JENIS_GAJI,
                        GAJI_KOMPONEN.NILAI,
                        GAJI_KOMPONEN.IS_REFERENCE,
                        GAJI_KOMPONEN.FORMULA,
                        GAJI_PROFIL.NAMA)
                .from(GAJI_KOMPONEN)
                .leftJoin(GAJI_PROFIL).on(GAJI_KOMPONEN.PROFIL_GAJI_ID.eq(GAJI_PROFIL.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiKomponenJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<GajiKomponenResponse> getById(Long id) {
        return dsl.select(
                        GAJI_KOMPONEN.ID,
                        GAJI_KOMPONEN.URUT,
                        GAJI_KOMPONEN.PROFIL_GAJI_ID,
                        GAJI_KOMPONEN.KODE,
                        GAJI_KOMPONEN.NAMA,
                        GAJI_KOMPONEN.JENIS_GAJI,
                        GAJI_KOMPONEN.NILAI,
                        GAJI_KOMPONEN.IS_REFERENCE,
                        GAJI_KOMPONEN.FORMULA,
                        GAJI_PROFIL.NAMA)
                .from(GAJI_KOMPONEN)
                .leftJoin(GAJI_PROFIL).on(GAJI_KOMPONEN.PROFIL_GAJI_ID.eq(GAJI_PROFIL.ID))
                .where(GAJI_KOMPONEN.ID.eq(id))
                .and(GAJI_KOMPONEN.IS_DELETED.eq(false))
                .fetchOptional(GajiKomponenJooqMapper::mapToResponse);
    }

    public List<GajiKomponenMiniProjection> findAllKode(Long profilId) {
        return dsl.selectDistinct(GAJI_KOMPONEN.KODE, GAJI_KOMPONEN.NAMA)
                .from(GAJI_KOMPONEN)
                .where(GAJI_KOMPONEN.PROFIL_GAJI_ID.eq(profilId))
                .and(GAJI_KOMPONEN.IS_DELETED.eq(false))
                .fetch(GajiKomponenJooqMapper::mapToMiniProjection);
    }

    public Integer findLastUrut(Long profilId) {
        return dsl.select(GAJI_KOMPONEN.URUT)
                .from(GAJI_KOMPONEN)
                .where(GAJI_KOMPONEN.PROFIL_GAJI_ID.eq(profilId))
                .and(GAJI_KOMPONEN.IS_DELETED.eq(false))
                .orderBy(GAJI_KOMPONEN.URUT.desc())
                .limit(1)
                .fetchOptional(0, Integer.class)
                .orElse(0);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "urut", GAJI_KOMPONEN.URUT,
                "kode", GAJI_KOMPONEN.KODE,
                "nama", GAJI_KOMPONEN.NAMA,
                "jenisGaji", GAJI_KOMPONEN.JENIS_GAJI,
                "nilai", GAJI_KOMPONEN.NILAI
        );
    }

    private Condition baseWhere(Long profilId, GajiKomponenIndexQuery q) {
        return GAJI_KOMPONEN.IS_DELETED.eq(false)
                .and(GAJI_KOMPONEN.PROFIL_GAJI_ID.eq(profilId))
                .and(q.getKode() != null && !q.getKode().isBlank() ? GAJI_KOMPONEN.KODE.likeIgnoreCase("%" + q.getKode() + "%") : DSL.noCondition());
    }
}
