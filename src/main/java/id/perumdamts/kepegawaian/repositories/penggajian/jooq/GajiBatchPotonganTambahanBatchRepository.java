package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanItem;
import id.perumdamts.kepegawaian.jooq.enums.GajiBatchMasterProsesJenisGaji;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMaster.GAJI_BATCH_MASTER;
import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMasterProses.GAJI_BATCH_MASTER_PROSES;

@Repository
@RequiredArgsConstructor
public class GajiBatchPotonganTambahanBatchRepository {
    private final DSLContext dsl;

    public Map<String, Long> findMasterIdsByNipams(String rootBatchId, Collection<String> nipams) {
        if (nipams == null || nipams.isEmpty()) {
            return Collections.emptyMap();
        }
        return dsl.select(GAJI_BATCH_MASTER.ID, GAJI_BATCH_MASTER.NIPAM)
                .from(GAJI_BATCH_MASTER)
                .where(GAJI_BATCH_MASTER.BATCH_ROOT_ID.eq(rootBatchId)
                        .and(GAJI_BATCH_MASTER.NIPAM.in(nipams)))
                .fetchMap(GAJI_BATCH_MASTER.NIPAM, GAJI_BATCH_MASTER.ID);
    }

    public Set<Long> findMasterIdsWithAddRows(String rootBatchId) {
        if (rootBatchId == null || rootBatchId.isBlank()) {
            return Collections.emptySet();
        }
        return dsl.selectDistinct(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID)
                .from(GAJI_BATCH_MASTER_PROSES)
                .join(GAJI_BATCH_MASTER)
                .on(GAJI_BATCH_MASTER.ID.eq(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID))
                .where(GAJI_BATCH_MASTER.BATCH_ROOT_ID.eq(rootBatchId)
                        .and(GAJI_BATCH_MASTER_PROSES.KODE.startsWith("ADD_")))
                .fetchSet(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID);
    }

    @Transactional
    public int deleteByRootBatchId(String rootBatchId) {
        if (rootBatchId == null || rootBatchId.isBlank()) {
            return 0;
        }
        List<Long> ids = dsl.select(GAJI_BATCH_MASTER.ID)
                .from(GAJI_BATCH_MASTER)
                .where(GAJI_BATCH_MASTER.BATCH_ROOT_ID.eq(rootBatchId))
                .fetchInto(Long.class);
        Set<Long> masterIds = new java.util.HashSet<>(ids);
        if (masterIds.isEmpty()) {
            return 0;
        }
        return dsl.deleteFrom(GAJI_BATCH_MASTER_PROSES)
                .where(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID.in(masterIds)
                        .and(GAJI_BATCH_MASTER_PROSES.KODE.startsWith("ADD_")))
                .execute();
    }

    @Transactional
    public int batchInsert(String rootBatchId, List<GajiBatchPotonganTambahanItem> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        // resolve batchMasterId dalam satu query (nipam -> id), bukan N+1 per baris
        Set<String> nipams = items.stream().map(GajiBatchPotonganTambahanItem::nipam).collect(Collectors.toSet());
        Map<String, Long> masterIds = findMasterIdsByNipams(rootBatchId, nipams);

        int chunkSize = 1000;
        int totalInserted = 0;
        for (int i = 0; i < items.size(); i += chunkSize) {
            List<GajiBatchPotonganTambahanItem> chunk = items.subList(i, Math.min(i + chunkSize, items.size()));
            var insertStep = dsl.insertInto(GAJI_BATCH_MASTER_PROSES,
                    GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID,
                    GAJI_BATCH_MASTER_PROSES.KODE,
                    GAJI_BATCH_MASTER_PROSES.URUT,
                    GAJI_BATCH_MASTER_PROSES.NAMA,
                    GAJI_BATCH_MASTER_PROSES.JENIS_GAJI,
                    GAJI_BATCH_MASTER_PROSES.NILAI);
            for (var item : chunk) {
                Long masterId = masterIds.get(item.nipam());
                if (masterId == null) {
                    continue;
                }
                insertStep = insertStep.values(
                        masterId,
                        item.kode(),
                        99,
                        item.nama(),
                        GajiBatchMasterProsesJenisGaji.POTONGAN,
                        item.nilai());
            }
            totalInserted += insertStep.execute();
        }
        return totalInserted;
    }
}