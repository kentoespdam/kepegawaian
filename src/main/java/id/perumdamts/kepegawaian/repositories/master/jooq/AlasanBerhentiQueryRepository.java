package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiQuery;
import id.perumdamts.kepegawaian.jooq.tables.AlasanBerhenti;
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
public class AlasanBerhentiQueryRepository {
    private final DSLContext dsl;

    public Page<AlasanBerhentiQuery> pageQuery(AlasanBerhentiIndexQuery query) {
        // Sort whitelist
        var sortField = switch (query.getSortBy()) {
            case "nama" -> AlasanBerhenti.ALASAN_BERHENTI.NAMA;
            default -> AlasanBerhenti.ALASAN_BERHENTI.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        // Count query
        var count = dsl.selectCount()
                .from(AlasanBerhenti.ALASAN_BERHENTI)
                .where(AlasanBerhenti.ALASAN_BERHENTI.IS_DELETED.eq(false))
                .and(query.getNama() != null ? AlasanBerhenti.ALASAN_BERHENTI.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        // Data query
        var data = dsl.select(AlasanBerhenti.ALASAN_BERHENTI.ID, AlasanBerhenti.ALASAN_BERHENTI.NAMA, AlasanBerhenti.ALASAN_BERHENTI.NOTES)
                .from(AlasanBerhenti.ALASAN_BERHENTI)
                .where(AlasanBerhenti.ALASAN_BERHENTI.IS_DELETED.eq(false))
                .and(query.getNama() != null ? AlasanBerhenti.ALASAN_BERHENTI.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(AlasanBerhentiQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<AlasanBerhentiQuery> getById(Long id) {
        return dsl.select(AlasanBerhenti.ALASAN_BERHENTI.ID, AlasanBerhenti.ALASAN_BERHENTI.NAMA, AlasanBerhenti.ALASAN_BERHENTI.NOTES)
                .from(AlasanBerhenti.ALASAN_BERHENTI)
                .where(AlasanBerhenti.ALASAN_BERHENTI.ID.eq(id))
                .and(AlasanBerhenti.ALASAN_BERHENTI.IS_DELETED.eq(false))
                .fetchOptionalInto(AlasanBerhentiQuery.class);
    }

    public List<AlasanBerhentiQuery> listQuery() {
        return dsl.select(AlasanBerhenti.ALASAN_BERHENTI.ID, AlasanBerhenti.ALASAN_BERHENTI.NAMA, AlasanBerhenti.ALASAN_BERHENTI.NOTES)
                .from(AlasanBerhenti.ALASAN_BERHENTI)
                .where(AlasanBerhenti.ALASAN_BERHENTI.IS_DELETED.eq(false))
                .orderBy(AlasanBerhenti.ALASAN_BERHENTI.NAMA.asc())
                .fetchInto(AlasanBerhentiQuery.class);
    }
}