package id.perumdamts.kepegawaian.services.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianQuery;
import id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisKeahlianQueries {
    private final DSLContext dsl;

    public Page<JenisKeahlianQuery> pageQuery(JenisKeahlianIndexQuery query) {
        // Sort whitelist
        var sortField = switch (query.getSortBy()) {
            case "nama" -> JenisKeahlian.JENIS_KEAHLIAN.NAMA;
            default -> JenisKeahlian.JENIS_KEAHLIAN.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        // Count query
        var count = dsl.selectCount()
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKeahlian.JENIS_KEAHLIAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        // Data query
        var data = dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKeahlian.JENIS_KEAHLIAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(JenisKeahlianQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<JenisKeahlianQuery> getById(Long id) {
        return dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.ID.eq(id))
                .and(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisKeahlianQuery.class);
    }

    public List<JenisKeahlianQuery> listQuery() {
        return dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .orderBy(JenisKeahlian.JENIS_KEAHLIAN.NAMA.asc())
                .fetchInto(JenisKeahlianQuery.class);
    }
}
