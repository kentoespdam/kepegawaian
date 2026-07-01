package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburIndexQuery;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.HariLibur.HARI_LIBUR;

@Repository
@RequiredArgsConstructor
public class HariLiburQueryRepository {
    private final DSLContext dsl;

    public Page<HariLiburQuery> pageQuery(HariLiburIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), HARI_LIBUR.ID);
        var count = dsl.selectCount()
                .from(HARI_LIBUR)
                .where(HARI_LIBUR.IS_DELETED.eq(false))
                .and(query.getJenisLibur() != null ? HARI_LIBUR.JENIS_LIBUR.eq(findJenisLiburOrdinal(query.getJenisLibur())) : DSL.noCondition())
                .and(query.getTahun() != null ? DSL.year(HARI_LIBUR.TANGGAL).eq(query.getTahun()) : DSL.noCondition())
                .and(query.getBulan() != null ? DSL.month(HARI_LIBUR.TANGGAL).eq(query.getBulan()) : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        HARI_LIBUR.ID,
                        HARI_LIBUR.TANGGAL,
                        HARI_LIBUR.JENIS_LIBUR,
                        HARI_LIBUR.NOTES)
                .from(HARI_LIBUR)
                .where(HARI_LIBUR.IS_DELETED.eq(false))
                .and(query.getJenisLibur() != null ? HARI_LIBUR.JENIS_LIBUR.eq(findJenisLiburOrdinal(query.getJenisLibur())) : DSL.noCondition())
                .and(query.getTahun() != null ? DSL.year(HARI_LIBUR.TANGGAL).eq(query.getTahun()) : DSL.noCondition())
                .and(query.getBulan() != null ? DSL.month(HARI_LIBUR.TANGGAL).eq(query.getBulan()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tanggal", HARI_LIBUR.TANGGAL,
                "jenisLibur", HARI_LIBUR.JENIS_LIBUR
        );
    }

    public Optional<HariLiburQuery> getById(Long id) {
        return dsl.select(
                        HARI_LIBUR.ID,
                        HARI_LIBUR.TANGGAL,
                        HARI_LIBUR.JENIS_LIBUR,
                        HARI_LIBUR.NOTES)
                .from(HARI_LIBUR)
                .where(HARI_LIBUR.ID.eq(id))
                .and(HARI_LIBUR.IS_DELETED.eq(false))
                .fetchOptional(record -> toQuery(record.intoMap()));
    }

    public List<HariLiburQuery> listQuery() {
        return dsl.select(
                        HARI_LIBUR.ID,
                        HARI_LIBUR.TANGGAL,
                        HARI_LIBUR.JENIS_LIBUR,
                        HARI_LIBUR.NOTES)
                .from(HARI_LIBUR)
                .where(HARI_LIBUR.IS_DELETED.eq(false))
                .orderBy(HARI_LIBUR.TANGGAL.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    private static Byte findJenisLiburOrdinal(String displayName) {
        for (EJenisLibur e : EJenisLibur.values()) {
            if (e.getValue().equals(displayName))
                return (byte) e.ordinal();
        }
        return null;
    }

    private static String jenisLiburByteToString(Byte ordinal) {
        if (ordinal == null) return null;
        for (EJenisLibur e : EJenisLibur.values()) {
            if (e.ordinal() == ordinal)
                return e.getValue();
        }
        return null;
    }

    private HariLiburQuery toQuery(Map<String, Object> map) {
        var query = new HariLiburQuery();
        query.setId((Long) map.get("id"));
        query.setTanggal((LocalDate) map.get("tanggal"));
        query.setJenisLibur(jenisLiburByteToString((Byte) map.get("jenis_libur")));
        query.setNotes((String) map.get("notes"));
        return query;
    }
}
