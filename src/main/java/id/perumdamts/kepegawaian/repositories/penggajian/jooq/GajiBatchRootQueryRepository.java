package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLog.GajiBatchRootErrorLogsResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran.GajiBatchRootLampiranMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot.GajiBatchRootJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRoot.GAJI_BATCH_ROOT;
import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRootErrorLogs.GAJI_BATCH_ROOT_ERROR_LOGS;
import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRootLampiran.GAJI_BATCH_ROOT_LAMPIRAN;

@Repository
@RequiredArgsConstructor
public class GajiBatchRootQueryRepository {
    private final DSLContext dsl;

    public Page<GajiBatchRootResponse> pageQuery(GajiBatchRootIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_ROOT.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_BATCH_ROOT)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.selectFrom(GAJI_BATCH_ROOT)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiBatchRootJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiBatchRootResponse> listQuery(GajiBatchRootIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_ROOT.ID);
        return dsl.selectFrom(GAJI_BATCH_ROOT)
                .where(baseWhere(query))
                .orderBy(sortOrder)
                .fetch(GajiBatchRootJooqMapper::mapToResponse);
    }

    public Optional<GajiBatchRootResponse> getById(String id) {
        return dsl.selectFrom(GAJI_BATCH_ROOT)
                .where(GAJI_BATCH_ROOT.ID.eq(id))
                .and(GAJI_BATCH_ROOT.IS_DELETED.eq(false))
                .fetchOptional(GajiBatchRootJooqMapper::mapToResponse)
                .map(response -> {
                    List<GajiBatchRootErrorLogsResponse> errorLogs = dsl.selectFrom(GAJI_BATCH_ROOT_ERROR_LOGS)
                            .where(GAJI_BATCH_ROOT_ERROR_LOGS.ROOT_BATCH_ID.eq(id))
                            .fetch(record -> new GajiBatchRootErrorLogsResponse(
                                    record.get(GAJI_BATCH_ROOT_ERROR_LOGS.ID),
                                    record.get(GAJI_BATCH_ROOT_ERROR_LOGS.NIPAM),
                                    record.get(GAJI_BATCH_ROOT_ERROR_LOGS.NAMA),
                                    record.get(GAJI_BATCH_ROOT_ERROR_LOGS.NOTES)
                            ));

                    List<GajiBatchRootLampiranMiniResponse> lampirans = dsl.selectFrom(GAJI_BATCH_ROOT_LAMPIRAN)
                            .where(GAJI_BATCH_ROOT_LAMPIRAN.ROOT_BATCH_ID.eq(id))
                            .fetch(record -> {
                                var jenisByte = record.get(GAJI_BATCH_ROOT_LAMPIRAN.JENIS_LAMPIRAN_GAJI);
                                EJenisPotonganGaji jenis = jenisByte != null ? EJenisPotonganGaji.values()[jenisByte.intValue()] : null;
                                return new GajiBatchRootLampiranMiniResponse(
                                        record.get(GAJI_BATCH_ROOT_LAMPIRAN.ID),
                                        jenis,
                                        record.get(GAJI_BATCH_ROOT_LAMPIRAN.FILE_NAME),
                                        record.get(GAJI_BATCH_ROOT_LAMPIRAN.MIME_TYPE)
                                );
                            });

                    return new GajiBatchRootResponse(
                            response.id(),
                            response.periode(),
                            response.status(),
                            response.totalPegawai(),
                            response.tanggalProses(),
                            response.diProsesOleh(),
                            response.jabatanPemroses(),
                            response.tanggalVerifikasiTahap1(),
                            response.diVerifikasiOlehTahap1(),
                            response.jabatanVerifikasiTahap1(),
                            response.tanggalVerifikasiTahap2(),
                            response.diVerifikasiOlehTahap2(),
                            response.jabatanVerifikasiTahap2(),
                            response.tanggalPersetujuan(),
                            response.diSetujuiOleh(),
                            response.jabatanPenyetuju(),
                            response.notes(),
                            errorLogs,
                            lampirans
                    );
                });
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", GAJI_BATCH_ROOT.ID,
                "periode", GAJI_BATCH_ROOT.PERIODE
        );
    }

    private Condition baseWhere(GajiBatchRootIndexQuery q) {
        Condition condition = GAJI_BATCH_ROOT.IS_DELETED.eq(false);
        if (q.getPeriode() != null && !q.getPeriode().isBlank()) {
            condition = condition.and(GAJI_BATCH_ROOT.PERIODE.likeIgnoreCase("%" + q.getPeriode() + "%"));
        }
        if (q.getStatus() != null) {
            condition = condition.and(GAJI_BATCH_ROOT.STATUS.eq(q.getStatus().ordinal()));
        }
        if (q.getLtStatus() != null && !q.getLtStatus().isBlank()) {
            try {
                int statusVal = EProsesGaji.valueOf(q.getLtStatus()).ordinal();
                condition = condition.and(GAJI_BATCH_ROOT.STATUS.le(statusVal));
            } catch (Exception ignored) {}
        }
        if (q.getGtStatus() != null && !q.getGtStatus().isBlank()) {
            try {
                int statusVal = EProsesGaji.valueOf(q.getGtStatus()).ordinal();
                condition = condition.and(GAJI_BATCH_ROOT.STATUS.ge(statusVal));
            } catch (Exception ignored) {}
        }
        return condition;
    }
}
