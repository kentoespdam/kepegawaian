package id.perumdamts.kepegawaian.services.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanQuery;
import id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan;
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
public class JenisPelatihanQueries {
    private final DSLContext dsl;

    public Page<JenisPelatihanQuery> pageQuery(JenisPelatihanIndexQuery query) {
        var sortField = switch (query.getSortBy()) {
            case "nama" -> JenisPelatihan.JENIS_PELATIHAN.NAMA;
            default -> JenisPelatihan.JENIS_PELATIHAN.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        var count = dsl.selectCount()
                .from(JenisPelatihan.JENIS_PELATIHAN)
                .where(JenisPelatihan.JENIS_PELATIHAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisPelatihan.JENIS_PELATIHAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        var data = dsl.select(JenisPelatihan.JENIS_PELATIHAN.ID, JenisPelatihan.JENIS_PELATIHAN.NAMA)
                .from(JenisPelatihan.JENIS_PELATIHAN)
                .where(JenisPelatihan.JENIS_PELATIHAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisPelatihan.JENIS_PELATIHAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(JenisPelatihanQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<JenisPelatihanQuery> getById(Long id) {
        return dsl.select(JenisPelatihan.JENIS_PELATIHAN.ID, JenisPelatihan.JENIS_PELATIHAN.NAMA)
                .from(JenisPelatihan.JENIS_PELATIHAN)
                .where(JenisPelatihan.JENIS_PELATIHAN.ID.eq(id))
                .and(JenisPelatihan.JENIS_PELATIHAN.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisPelatihanQuery.class);
    }

    public List<JenisPelatihanQuery> listQuery() {
        return dsl.select(JenisPelatihan.JENIS_PELATIHAN.ID, JenisPelatihan.JENIS_PELATIHAN.NAMA)
                .from(JenisPelatihan.JENIS_PELATIHAN)
                .where(JenisPelatihan.JENIS_PELATIHAN.IS_DELETED.eq(false))
                .orderBy(JenisPelatihan.JENIS_PELATIHAN.NAMA.asc())
                .fetchInto(JenisPelatihanQuery.class);
    }
}
