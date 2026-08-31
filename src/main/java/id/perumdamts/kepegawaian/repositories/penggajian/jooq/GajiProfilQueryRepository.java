package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiProfil.GajiProfilJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;

@Repository
@RequiredArgsConstructor
public class GajiProfilQueryRepository {
    private final DSLContext dsl;

    public Page<GajiProfilResponse> pageQuery(GajiProfilIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_PROFIL.ID);
        Condition where = baseWhere(query.getNama());
        var count = dsl.selectCount()
                .from(GAJI_PROFIL)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_PROFIL.ID,
                        GAJI_PROFIL.NAMA)
                .from(GAJI_PROFIL)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiProfilJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiProfilResponse> listQuery(GajiProfilListRequest query) {
        return dsl.select(
                        GAJI_PROFIL.ID,
                        GAJI_PROFIL.NAMA)
                .from(GAJI_PROFIL)
                .where(baseWhere(query.getNama()))
                .orderBy(GAJI_PROFIL.ID.asc())
                .fetch(GajiProfilJooqMapper::mapToResponse);
    }

    public Optional<GajiProfilResponse> getById(Long id) {
        return dsl.select(
                        GAJI_PROFIL.ID,
                        GAJI_PROFIL.NAMA)
                .from(GAJI_PROFIL)
                .where(GAJI_PROFIL.ID.eq(id))
                .and(GAJI_PROFIL.IS_DELETED.eq(false))
                .fetchOptional(GajiProfilJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", GAJI_PROFIL.NAMA
        );
    }

    private Condition baseWhere(String nama) {
        return GAJI_PROFIL.IS_DELETED.eq(false)
                .and(nama != null && !nama.isBlank() ? GAJI_PROFIL.NAMA.likeIgnoreCase("%" + nama + "%") : DSL.noCondition());
    }
}
