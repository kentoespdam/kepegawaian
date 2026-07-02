package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatKontrak.RIWAYAT_KONTRAK;

@Repository
@RequiredArgsConstructor
public class RiwayatKontrakQueryRepository {
    private final DSLContext dsl;

    public Page<RiwayatKontrakQuery> pageQuery(RiwayatKontrakRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_KONTRAK.TANGGAL_MULAI);

        var condition = getFilterCondition(request);

        var count = dsl.selectCount()
                .from(RIWAYAT_KONTRAK)
                .where(condition)
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.selectFrom(RIWAYAT_KONTRAK)
                .where(condition)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    public Optional<RiwayatKontrakQuery> getById(Long id) {
        return dsl.selectFrom(RIWAYAT_KONTRAK)
                .where(RIWAYAT_KONTRAK.ID.eq(id))
                .and(RIWAYAT_KONTRAK.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tanggalMulai", RIWAYAT_KONTRAK.TANGGAL_MULAI
        );
    }

    private org.jooq.Condition getFilterCondition(RiwayatKontrakRequest request) {
        var condition = RIWAYAT_KONTRAK.IS_DELETED.eq(false);
        if (request.getPegawaiId() != null) {
            condition = condition.and(RIWAYAT_KONTRAK.PEGAWAI_ID.eq(request.getPegawaiId()));
        }
        if (request.getNomorKontrak() != null) {
            condition = condition.and(RIWAYAT_KONTRAK.NOMOR_KONTRAK.likeIgnoreCase("%" + request.getNomorKontrak() + "%"));
        }
        return condition;
    }

    private RiwayatKontrakQuery toQuery(Record record) {
        RiwayatKontrakQuery query = new RiwayatKontrakQuery();
        query.setId(record.get(RIWAYAT_KONTRAK.ID));
        Byte jkByte = record.get(RIWAYAT_KONTRAK.JENIS_KONTRAK);
        if (jkByte != null) {
            query.setJenisKontrak(EJenisKontrak.values()[jkByte.intValue()]);
        }
        query.setNipam(record.get(RIWAYAT_KONTRAK.NIPAM));
        query.setNama(record.get(RIWAYAT_KONTRAK.NAMA));
        query.setNomorKontrak(record.get(RIWAYAT_KONTRAK.NOMOR_KONTRAK));
        query.setTanggalSk(record.get(RIWAYAT_KONTRAK.TANGGAL_SK));
        query.setTanggalMulai(record.get(RIWAYAT_KONTRAK.TANGGAL_MULAI));
        query.setTanggalSelesai(record.get(RIWAYAT_KONTRAK.TANGGAL_SELESAI));
        query.setNotes(record.get(RIWAYAT_KONTRAK.NOTES));
        return query;
    }
}
