package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisListRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import id.perumdamts.kepegawaian.mapper.cuti.CutiJenisJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;

@Repository
@RequiredArgsConstructor
public class CutiJenisQueryRepository {
    private final DSLContext dsl;

    public Page<CutiJenisResponse> pageQuery(CutiJenisRequest query) {
        var parent = CUTI_JENIS.as("parent");
        
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_JENIS.ID);
                
        Condition where = baseWhere(query.getParentId(), query.getNama());
        var count = dsl.selectCount().from(CUTI_JENIS).where(where).fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(
                        CUTI_JENIS.ID,
                        CUTI_JENIS.NAMA,
                        CUTI_JENIS.MAX_HARI,
                        CUTI_JENIS.POTONG_KUOTA_TAHUNAN,
                        parent.ID.as("parent_id"),
                        parent.NAMA.as("parent_nama")
                )
                .from(CUTI_JENIS)
                .leftJoin(parent).on(CUTI_JENIS.PARENT_ID.eq(parent.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(CutiJenisJooqMapper::mapToResponse);
                
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<CutiJenisMiniResponse> listQuery(CutiJenisListRequest query) {
        Condition where = baseWhere(query.getParentId(), query.getNama());

        return dsl.select(
                        CUTI_JENIS.ID,
                        CUTI_JENIS.NAMA,
                        CUTI_JENIS.PARENT_ID
                )
                .from(CUTI_JENIS)
                .where(where)
                .orderBy(CUTI_JENIS.ID.asc())
                .fetch(CutiJenisJooqMapper::mapToMini);
    }

    public CutiJenisResponse getById(Long id) {
        var parent = CUTI_JENIS.as("parent");
        
        return dsl.select(
                        CUTI_JENIS.ID,
                        CUTI_JENIS.NAMA,
                        CUTI_JENIS.MAX_HARI,
                        CUTI_JENIS.POTONG_KUOTA_TAHUNAN,
                        parent.ID.as("parent_id"),
                        parent.NAMA.as("parent_nama")
                )
                .from(CUTI_JENIS)
                .leftJoin(parent).on(CUTI_JENIS.PARENT_ID.eq(parent.ID))
                .where(CUTI_JENIS.ID.eq(id).and(CUTI_JENIS.IS_DELETED.eq(false)))
                .fetchOne(CutiJenisJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", CUTI_JENIS.NAMA,
                "maxHari", CUTI_JENIS.MAX_HARI,
                "potongKuotaTahunan", CUTI_JENIS.POTONG_KUOTA_TAHUNAN
        );
    }

    private Condition baseWhere(Long parentId, String nama) {
        return CUTI_JENIS.IS_DELETED.eq(false)
                .and(parentId != null ? CUTI_JENIS.PARENT_ID.eq(parentId) : DSL.noCondition())
                .and(nama != null ? CUTI_JENIS.NAMA.likeIgnoreCase("%" + nama + "%") : DSL.noCondition());
    }
}
