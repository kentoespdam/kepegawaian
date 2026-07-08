package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanListResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanQuery;
import id.perumdamts.kepegawaian.mapper.master.jabatan.JabatanJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import id.perumdamts.kepegawaian.jooq.tables.Jabatan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

@Repository
@RequiredArgsConstructor
public class JabatanQueryRepository {
    private final DSLContext dsl;

    private Field<?>[] queryColumns(Jabatan parentAlias) {
        var fields = new ArrayList<Field<?>>();
        Collections.addAll(fields, JabatanSelects.JABATAN_COLUMNS);
        Collections.addAll(fields, JabatanSelects.parentColumns(parentAlias));
        Collections.addAll(fields,
                SharedSelects.ORG_ID,
                SharedSelects.ORG_KODE,
                SharedSelects.ORG_NAMA,
                SharedSelects.ORG_SHORT_NAME,
                SharedSelects.LEVEL_ID,
                SharedSelects.LEVEL_NAMA
        );
        return fields.toArray(Field<?>[]::new);
    }

    public Page<JabatanQuery> pageQuery(JabatanIndexQuery query) {
        var parent = JABATAN.as("parent");
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JABATAN.ID);
        var count = dsl.selectCount()
                .from(JABATAN)
                .where(JABATAN.IS_DELETED.eq(false))
                .and(query.getKode() != null ? JABATAN.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getNama() != null ? JABATAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .and(query.getParentId() != null ? JABATAN.PARENT_ID.eq(query.getParentId()) : DSL.noCondition())
                .and(query.getOrganisasiId() != null ? JABATAN.ORGANISASI_ID.eq(query.getOrganisasiId()) : DSL.noCondition())
                .and(query.getLevelId() != null ? JABATAN.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(queryColumns(parent))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.IS_DELETED.eq(false))
                .and(query.getKode() != null ? JABATAN.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getNama() != null ? JABATAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .and(query.getParentId() != null ? JABATAN.PARENT_ID.eq(query.getParentId()) : DSL.noCondition())
                .and(query.getOrganisasiId() != null ? JABATAN.ORGANISASI_ID.eq(query.getOrganisasiId()) : DSL.noCondition())
                .and(query.getLevelId() != null ? JABATAN.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(JabatanJooqMapper::toQuery);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<JabatanQuery> getById(Long id) {
        var parent = JABATAN.as("parent");
        return dsl.select(queryColumns(parent))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.ID.eq(id))
                .and(JABATAN.IS_DELETED.eq(false))
                .fetchOptional(JabatanJooqMapper::toQuery);
    }

    public List<JabatanListResponse> listQuery() {
        return dsl.select(JABATAN.ID, JABATAN.NAMA)
                .from(JABATAN)
                .where(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetchInto(JabatanListResponse.class);
    }

    public List<JabatanQuery> findByParentId(Long parentId) {
        var parent = JABATAN.as("parent");
        return dsl.select(queryColumns(parent))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.PARENT_ID.eq(parentId))
                .and(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetch(JabatanJooqMapper::toQuery);
    }

    public List<JabatanQuery> findByOrganisasiId(Long organisasiId) {
        var parent = JABATAN.as("parent");
        return dsl.select(queryColumns(parent))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.ORGANISASI_ID.eq(organisasiId))
                .and(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetch(JabatanJooqMapper::toQuery);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", JABATAN.KODE,
                "nama", JABATAN.NAMA,
                "levelId", JABATAN.LEVEL_ID,
                "organisasiId", JABATAN.ORGANISASI_ID
        );
    }
}
