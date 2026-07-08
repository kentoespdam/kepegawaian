package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiListResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiQuery;
import id.perumdamts.kepegawaian.mapper.master.organisasi.OrganisasiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import id.perumdamts.kepegawaian.jooq.tables.Organisasi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

@Repository
@RequiredArgsConstructor
public class OrganisasiQueryRepository {
    private final DSLContext dsl;

    private Field<?>[] queryColumns(Organisasi parentAlias) {
        var fields = new ArrayList<Field<?>>();
        Collections.addAll(fields, OrganisasiSelects.ORGANISASI_COLUMNS);
        Collections.addAll(fields, OrganisasiSelects.parentColumns(parentAlias));
        return fields.toArray(Field<?>[]::new);
    }

    public Page<OrganisasiQuery> pageQuery(OrganisasiIndexQuery query) {
        var parent = ORGANISASI.as("parent");
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), ORGANISASI.ID);
        var count = dsl.selectCount()
                .from(ORGANISASI)
                .where(ORGANISASI.IS_DELETED.eq(false))
                .and(query.getKode() != null ? ORGANISASI.KODE.likeIgnoreCase("%" + query.getKode() + "%") : DSL.noCondition())
                .and(query.getNama() != null ? ORGANISASI.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .and(query.getParentId() != null ? ORGANISASI.PARENT_ID.eq(query.getParentId()) : DSL.noCondition())
                .and(query.getLevelOrg() != null ? ORGANISASI.LEVEL_ORG.eq(query.getLevelOrg()) : DSL.noCondition())
                .and(query.getCategory() != null ? ORGANISASI.CATEGORY.eq(query.getCategory()) : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(queryColumns(parent))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.IS_DELETED.eq(false))
                .and(query.getKode() != null ? ORGANISASI.KODE.likeIgnoreCase("%" + query.getKode() + "%") : DSL.noCondition())
                .and(query.getNama() != null ? ORGANISASI.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .and(query.getParentId() != null ? ORGANISASI.PARENT_ID.eq(query.getParentId()) : DSL.noCondition())
                .and(query.getLevelOrg() != null ? ORGANISASI.LEVEL_ORG.eq(query.getLevelOrg()) : DSL.noCondition())
                .and(query.getCategory() != null ? ORGANISASI.CATEGORY.eq(query.getCategory()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(OrganisasiJooqMapper::toQuery);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<OrganisasiQuery> getById(Long id) {
        var parent = ORGANISASI.as("parent");
        return dsl.select(queryColumns(parent))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.ID.eq(id))
                .and(ORGANISASI.IS_DELETED.eq(false))
                .fetchOptional(OrganisasiJooqMapper::toQuery);
    }

    public List<OrganisasiListResponse> listQuery() {
        return dsl.select(OrganisasiSelects.ID, OrganisasiSelects.NAMA)
                .from(ORGANISASI)
                .where(ORGANISASI.IS_DELETED.eq(false))
                .orderBy(ORGANISASI.NAMA.asc())
                .fetchInto(OrganisasiListResponse.class);
    }

    public List<OrganisasiQuery> findByParentId(Long parentId) {
        var parent = ORGANISASI.as("parent");
        return dsl.select(queryColumns(parent))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.PARENT_ID.eq(parentId))
                .and(ORGANISASI.IS_DELETED.eq(false))
                .orderBy(ORGANISASI.NAMA.asc())
                .fetch(OrganisasiJooqMapper::toQuery);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", ORGANISASI.KODE,
                "nama", ORGANISASI.NAMA,
                "levelOrg", ORGANISASI.LEVEL_ORG,
                "shortName", ORGANISASI.SHORT_NAME,
                "category", ORGANISASI.CATEGORY
        );
    }


}
