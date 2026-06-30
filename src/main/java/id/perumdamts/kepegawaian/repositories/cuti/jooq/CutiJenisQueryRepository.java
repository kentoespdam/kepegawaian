package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
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
                
        Condition where = baseWhere(query);
        var count = dsl.selectCount().from(CUTI_JENIS).where(where).fetchOne(0, Long.class);
        
        int pageNumber = query.getPage() != null ? query.getPage() : 0;
        int sizeOrDefault = query.getSize() != null ? query.getSize() : 10;

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
                .limit(sizeOrDefault)
                .offset(pageNumber * sizeOrDefault)
                .fetch(record -> {
                    CutiJenisResponse response = new CutiJenisResponse();
                    response.setId(record.get(CUTI_JENIS.ID));
                    response.setNama(record.get(CUTI_JENIS.NAMA));
                    response.setMaxHari(record.get(CUTI_JENIS.MAX_HARI));
                    response.setPotongKuotaTahunan(record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN));
                    if (record.get("parent_id") != null) {
                        CutiJenisMiniResponse mini = new CutiJenisMiniResponse();
                        mini.setId((Long) record.get("parent_id"));
                        mini.setNama((String) record.get("parent_nama"));
                        response.setParent(mini);
                    }
                    return response;
                });
                
        return new PageImpl<>(data, PageRequest.of(pageNumber, sizeOrDefault), count);
    }

    public List<CutiJenisResponse> listQuery(CutiJenisRequest query) {
        var parent = CUTI_JENIS.as("parent");
        Condition where = baseWhere(query);
        
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
                .where(where)
                .orderBy(CUTI_JENIS.NAMA.asc())
                .fetch(record -> {
                    CutiJenisResponse response = new CutiJenisResponse();
                    response.setId(record.get(CUTI_JENIS.ID));
                    response.setNama(record.get(CUTI_JENIS.NAMA));
                    response.setMaxHari(record.get(CUTI_JENIS.MAX_HARI));
                    response.setPotongKuotaTahunan(record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN));
                    if (record.get("parent_id") != null) {
                        CutiJenisMiniResponse mini = new CutiJenisMiniResponse();
                        mini.setId((Long) record.get("parent_id"));
                        mini.setNama((String) record.get("parent_nama"));
                        response.setParent(mini);
                    }
                    return response;
                });
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
                .fetchOne(record -> {
                    CutiJenisResponse response = new CutiJenisResponse();
                    response.setId(record.get(CUTI_JENIS.ID));
                    response.setNama(record.get(CUTI_JENIS.NAMA));
                    response.setMaxHari(record.get(CUTI_JENIS.MAX_HARI));
                    response.setPotongKuotaTahunan(record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN));
                    if (record.get("parent_id") != null) {
                        CutiJenisMiniResponse mini = new CutiJenisMiniResponse();
                        mini.setId((Long) record.get("parent_id"));
                        mini.setNama((String) record.get("parent_nama"));
                        response.setParent(mini);
                    }
                    return response;
                });
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", CUTI_JENIS.NAMA,
                "maxHari", CUTI_JENIS.MAX_HARI,
                "potongKuotaTahunan", CUTI_JENIS.POTONG_KUOTA_TAHUNAN
        );
    }

    private Condition baseWhere(CutiJenisRequest q) {
        return CUTI_JENIS.IS_DELETED.eq(false)
                .and(q.getParentId() != null ? CUTI_JENIS.PARENT_ID.eq(q.getParentId()) : DSL.noCondition())
                .and(q.getNama() != null ? CUTI_JENIS.NAMA.likeIgnoreCase("%" + q.getNama() + "%") : DSL.noCondition());
    }
}
