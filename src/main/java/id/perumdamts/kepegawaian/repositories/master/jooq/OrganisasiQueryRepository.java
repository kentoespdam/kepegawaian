package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiQuery;
import id.perumdamts.kepegawaian.dto.commons.SortParam;
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
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

@Repository
@RequiredArgsConstructor
public class OrganisasiQueryRepository {
    private final DSLContext dsl;

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
                .fetchOne(0, Long.class);
        var data = dsl.select(
                        ORGANISASI.ID,
                        ORGANISASI.KODE,
                        ORGANISASI.PARENT_ID.as("self_parent_id"),
                        ORGANISASI.LEVEL_ORG.as("levelOrganisasi"),
                        ORGANISASI.NAMA,
                        ORGANISASI.SHORT_NAME,
                        ORGANISASI.CATEGORY,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        parent.SHORT_NAME.as("parent_short_name"))
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
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<OrganisasiQuery> getById(Long id) {
        var parent = ORGANISASI.as("parent");
        return dsl.select(
                        ORGANISASI.ID,
                        ORGANISASI.KODE,
                        ORGANISASI.PARENT_ID.as("self_parent_id"),
                        ORGANISASI.LEVEL_ORG.as("levelOrganisasi"),
                        ORGANISASI.NAMA,
                        ORGANISASI.SHORT_NAME,
                        ORGANISASI.CATEGORY,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        parent.SHORT_NAME.as("parent_short_name"))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.ID.eq(id))
                .and(ORGANISASI.IS_DELETED.eq(false))
                .fetchOptional(record -> toQuery(record.intoMap()));
    }

    public List<OrganisasiQuery> listQuery() {
        var parent = ORGANISASI.as("parent");
        return dsl.select(
                        ORGANISASI.ID,
                        ORGANISASI.KODE,
                        ORGANISASI.PARENT_ID.as("self_parent_id"),
                        ORGANISASI.LEVEL_ORG.as("levelOrganisasi"),
                        ORGANISASI.NAMA,
                        ORGANISASI.SHORT_NAME,
                        ORGANISASI.CATEGORY,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        parent.SHORT_NAME.as("parent_short_name"))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.IS_DELETED.eq(false))
                .orderBy(ORGANISASI.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    public List<OrganisasiQuery> findByParentId(Long parentId) {
        var parent = ORGANISASI.as("parent");
        return dsl.select(
                        ORGANISASI.ID,
                        ORGANISASI.KODE,
                        ORGANISASI.PARENT_ID.as("self_parent_id"),
                        ORGANISASI.LEVEL_ORG.as("levelOrganisasi"),
                        ORGANISASI.NAMA,
                        ORGANISASI.SHORT_NAME,
                        ORGANISASI.CATEGORY,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        parent.SHORT_NAME.as("parent_short_name"))
                .from(ORGANISASI)
                .leftJoin(parent).on(ORGANISASI.PARENT_ID.eq(parent.ID))
                .where(ORGANISASI.PARENT_ID.eq(parentId))
                .and(ORGANISASI.IS_DELETED.eq(false))
                .orderBy(ORGANISASI.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
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

    private OrganisasiQuery toQuery(java.util.Map<String, Object> map) {
        var query = new OrganisasiQuery();
        query.setId((Long) map.get("id"));
        query.setKode((String) map.get("kode"));
        query.setParentId((Long) map.get("self_parent_id"));
        query.setLevelOrganisasi((Integer) map.get("levelOrganisasi"));
        query.setNama((String) map.get("nama"));
        query.setShortName((String) map.get("short_name"));
        query.setCategory((String) map.get("category"));
        if (map.get("parent_kode") != null || map.get("parent_nama") != null) {
            var parent = new OrganisasiMiniResponse();
            parent.setId((Long) map.get("parent_id"));
            parent.setKode((String) map.get("parent_kode"));
            parent.setNama((String) map.get("parent_nama"));
            parent.setShortName((String) map.get("parent_short_name"));
            query.setParent(parent);
        }
        return query;
    }
}
