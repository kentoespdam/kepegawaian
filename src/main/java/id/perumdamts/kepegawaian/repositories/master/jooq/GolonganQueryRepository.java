package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganIndexQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganListResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganQuery;
import id.perumdamts.kepegawaian.jooq.tables.Golongan;
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

@Repository
@RequiredArgsConstructor
public class GolonganQueryRepository {
    private final DSLContext dsl;

    public Page<GolonganQuery> pageQuery(GolonganIndexQuery query) {
        // Sort whitelist
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), Golongan.GOLONGAN.ID);

        // Count query
        var count = dsl.selectCount()
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .and(query.getGolongan() != null ? Golongan.GOLONGAN.GOLONGAN_.likeIgnoreCase("%" + query.getGolongan() + "%") : DSL.noCondition())
                .and(query.getPangkat() != null ? Golongan.GOLONGAN.PANGKAT.likeIgnoreCase("%" + query.getPangkat() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);

        // Data query
        var data = dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .and(query.getGolongan() != null ? Golongan.GOLONGAN.GOLONGAN_.likeIgnoreCase("%" + query.getGolongan() + "%") : DSL.noCondition())
                .and(query.getPangkat() != null ? Golongan.GOLONGAN.PANGKAT.likeIgnoreCase("%" + query.getPangkat() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetchInto(GolonganQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "golongan", Golongan.GOLONGAN.GOLONGAN_,
                "pangkat", Golongan.GOLONGAN.PANGKAT
        );
    }

    public Optional<GolonganQuery> getById(Long id) {
        return dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.ID.eq(id))
                .and(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .fetchOptionalInto(GolonganQuery.class);
    }

    public List<GolonganListResponse> listQuery() {
        return dsl.select(Golongan.GOLONGAN.ID, Golongan.GOLONGAN.GOLONGAN_, Golongan.GOLONGAN.PANGKAT)
                .from(Golongan.GOLONGAN)
                .where(Golongan.GOLONGAN.IS_DELETED.eq(false))
                .orderBy(Golongan.GOLONGAN.GOLONGAN_.asc())
                .fetchInto(GolonganListResponse.class);
    }
}
