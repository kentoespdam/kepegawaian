package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

@Service
@RequiredArgsConstructor
public class JabatanQueries {
    private final DSLContext dsl;

    public Page<JabatanQuery> pageQuery(JabatanIndexQuery query) {
        var parent = JABATAN.as("parent");
        var sortField = switch (query.getSortBy()) {
            case "kode" -> JABATAN.KODE;
            case "nama" -> JABATAN.NAMA;
            case "levelId" -> JABATAN.LEVEL_ID;
            case "organisasiId" -> JABATAN.ORGANISASI_ID;
            default -> JABATAN.ID;
        };
        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();
        var count = dsl.selectCount()
                .from(JABATAN)
                .where(JABATAN.IS_DELETED.eq(false))
                .and(query.getKode() != null ? JABATAN.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getNama() != null ? JABATAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .and(query.getParentId() != null ? JABATAN.PARENT_ID.eq(query.getParentId()) : DSL.noCondition())
                .and(query.getOrganisasiId() != null ? JABATAN.ORGANISASI_ID.eq(query.getOrganisasiId()) : DSL.noCondition())
                .and(query.getLevelId() != null ? JABATAN.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .fetchOne(0, Long.class);
        var data = dsl.select(
                        JABATAN.ID,
                        JABATAN.KODE,
                        JABATAN.PARENT_ID,
                        JABATAN.ORGANISASI_ID,
                        JABATAN.LEVEL_ID,
                        JABATAN.NAMA,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        ORGANISASI.SHORT_NAME.as("org_short_name"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
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
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<JabatanQuery> getById(Long id) {
        var parent = JABATAN.as("parent");
        return dsl.select(
                        JABATAN.ID,
                        JABATAN.KODE,
                        JABATAN.PARENT_ID,
                        JABATAN.ORGANISASI_ID,
                        JABATAN.LEVEL_ID,
                        JABATAN.NAMA,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        ORGANISASI.SHORT_NAME.as("org_short_name"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.ID.eq(id))
                .and(JABATAN.IS_DELETED.eq(false))
                .fetchOptional(record -> toQuery(record.intoMap()));
    }

    public List<JabatanQuery> listQuery() {
        var parent = JABATAN.as("parent");
        return dsl.select(
                        JABATAN.ID,
                        JABATAN.KODE,
                        JABATAN.PARENT_ID,
                        JABATAN.ORGANISASI_ID,
                        JABATAN.LEVEL_ID,
                        JABATAN.NAMA,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        ORGANISASI.SHORT_NAME.as("org_short_name"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    public List<JabatanQuery> findByParentId(Long parentId) {
        var parent = JABATAN.as("parent");
        return dsl.select(
                        JABATAN.ID,
                        JABATAN.KODE,
                        JABATAN.PARENT_ID,
                        JABATAN.ORGANISASI_ID,
                        JABATAN.LEVEL_ID,
                        JABATAN.NAMA,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        ORGANISASI.SHORT_NAME.as("org_short_name"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.PARENT_ID.eq(parentId))
                .and(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    public List<JabatanQuery> findByOrganisasiId(Long organisasiId) {
        var parent = JABATAN.as("parent");
        return dsl.select(
                        JABATAN.ID,
                        JABATAN.KODE,
                        JABATAN.PARENT_ID,
                        JABATAN.ORGANISASI_ID,
                        JABATAN.LEVEL_ID,
                        JABATAN.NAMA,
                        parent.ID.as("parent_id"),
                        parent.KODE.as("parent_kode"),
                        parent.NAMA.as("parent_nama"),
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        ORGANISASI.SHORT_NAME.as("org_short_name"),
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(JABATAN)
                .leftJoin(parent).on(JABATAN.PARENT_ID.eq(parent.ID))
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(LEVEL).on(JABATAN.LEVEL_ID.eq(LEVEL.ID))
                .where(JABATAN.ORGANISASI_ID.eq(organisasiId))
                .and(JABATAN.IS_DELETED.eq(false))
                .orderBy(JABATAN.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    private JabatanQuery toQuery(Map<String, Object> map) {
        var query = new JabatanQuery();
        query.setId((Long) map.get("id"));
        query.setKode((String) map.get("kode"));
        query.setParentId((Long) map.get("parent_id"));
        query.setOrganisasiId((Long) map.get("organisasi_id"));
        query.setLevelId((Long) map.get("level_id"));
        query.setNama((String) map.get("nama"));
        if (map.get("parent_kode") != null || map.get("parent_nama") != null) {
            var p = new JabatanMiniResponse();
            p.setId((Long) map.get("parent_id"));
            p.setKode((String) map.get("parent_kode"));
            p.setNama((String) map.get("parent_nama"));
            query.setParent(p);
        }
        if (map.get("org_id") != null) {
            var o = new OrganisasiMiniResponse();
            o.setId((Long) map.get("org_id"));
            o.setKode((String) map.get("org_kode"));
            o.setNama((String) map.get("org_nama"));
            o.setShortName((String) map.get("org_short_name"));
            query.setOrganisasi(o);
        }
        if (map.get("level_id") != null) {
            var l = new LevelResponse((Long) map.get("level_id"), (String) map.get("level_nama"));
            query.setLevel(l);
        }
        return query;
    }
}
