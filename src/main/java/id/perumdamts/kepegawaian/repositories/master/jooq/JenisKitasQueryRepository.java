package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasQuery;
import id.perumdamts.kepegawaian.jooq.tables.JenisKitas;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JenisKitasQueryRepository {
    private final DSLContext dsl;

    public Page<JenisKitasQuery> pageQuery(JenisKitasIndexQuery query) {
        var sortField = switch (query.getSortBy()) {
            case "nama" -> JenisKitas.JENIS_KITAS.NAMA;
            default -> JenisKitas.JENIS_KITAS.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        var count = dsl.selectCount()
                .from(JenisKitas.JENIS_KITAS)
                .where(JenisKitas.JENIS_KITAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKitas.JENIS_KITAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        var data = dsl.select(JenisKitas.JENIS_KITAS.ID, JenisKitas.JENIS_KITAS.NAMA)
                .from(JenisKitas.JENIS_KITAS)
                .where(JenisKitas.JENIS_KITAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKitas.JENIS_KITAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(JenisKitasQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<JenisKitasQuery> getById(Long id) {
        return dsl.select(JenisKitas.JENIS_KITAS.ID, JenisKitas.JENIS_KITAS.NAMA)
                .from(JenisKitas.JENIS_KITAS)
                .where(JenisKitas.JENIS_KITAS.ID.eq(id))
                .and(JenisKitas.JENIS_KITAS.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisKitasQuery.class);
    }

    public List<JenisKitasQuery> listQuery() {
        return dsl.select(JenisKitas.JENIS_KITAS.ID, JenisKitas.JENIS_KITAS.NAMA)
                .from(JenisKitas.JENIS_KITAS)
                .where(JenisKitas.JENIS_KITAS.IS_DELETED.eq(false))
                .orderBy(JenisKitas.JENIS_KITAS.NAMA.asc())
                .fetchInto(JenisKitasQuery.class);
    }
}
