package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganIndexQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganQuery;
import id.perumdamts.kepegawaian.jooq.tables.Golongan;
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
public class GolonganQueryRepository {
    private final DSLContext dsl;

    public Page<GolonganQuery> pageQuery(GolonganIndexQuery query) {
        // Sort whitelist
        var sortField = switch (query.getSortBy()) {
            case "golongan" -> Golongan.GOLONGAN.GOLONGAN_;
            case "pangkat" -> Golongan.GOLONGAN.PANGKAT;
            default -> Golongan.GOLONGAN.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        // Count query
        var count = dsl.selectCount()
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .and(query.getGolongan() != null ? Golongan.GOLONGAN.GOLONGAN_.likeIgnoreCase("%" + query.getGolongan() + "%") : DSL.noCondition())
                .and(query.getPangkat() != null ? Golongan.GOLONGAN.PANGKAT.likeIgnoreCase("%" + query.getPangkat() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        // Data query
        var data = dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .and(query.getGolongan() != null ? Golongan.GOLONGAN.GOLONGAN_.likeIgnoreCase("%" + query.getGolongan() + "%") : DSL.noCondition())
                .and(query.getPangkat() != null ? Golongan.GOLONGAN.PANGKAT.likeIgnoreCase("%" + query.getPangkat() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(GolonganQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<GolonganQuery> getById(Long id) {
        return dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.ID.eq(id))
                .and(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .fetchOptionalInto(GolonganQuery.class);
    }

    public List<GolonganQuery> listQuery() {
        return dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .orderBy(Golongan.GOLONGAN.GOLONGAN_.asc())
                .fetchInto(GolonganQuery.class);
    }
}
