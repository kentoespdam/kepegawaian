package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchPotonganTkk.GAJI_BATCH_POTONGAN_TKK;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class GajiBatchPotonganTkkBatchRepository {
    private final DSLContext dsl;

    public Set<String> findExistingPegawaiNipams(Collection<String> nipams) {
        if (nipams == null || nipams.isEmpty()) {
            return Collections.emptySet();
        }
        return dsl.select(PEGAWAI.NIPAM)
                .from(PEGAWAI)
                .where(PEGAWAI.NIPAM.in(nipams))
                .fetchSet(PEGAWAI.NIPAM);
    }

    @Transactional
    public int deleteByBatchId(String batchId) {
        if (batchId == null || batchId.isBlank()) {
            return 0;
        }
        return dsl.deleteFrom(GAJI_BATCH_POTONGAN_TKK)
                .where(GAJI_BATCH_POTONGAN_TKK.BATCH_ID.eq(batchId))
                .execute();
    }

    @Transactional
    public int batchInsert(String batchId, List<GajiBatchPotonganTkkItem> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int chunkSize = 1000;
        int totalInserted = 0;

        for (int i = 0; i < items.size(); i += chunkSize) {
            List<GajiBatchPotonganTkkItem> chunk = items.subList(i, Math.min(i + chunkSize, items.size()));
            var insertStep = dsl.insertInto(GAJI_BATCH_POTONGAN_TKK,
                    GAJI_BATCH_POTONGAN_TKK.BATCH_ID,
                    GAJI_BATCH_POTONGAN_TKK.NIPAM,
                    GAJI_BATCH_POTONGAN_TKK.POTONGAN
            );
            for (var item : chunk) {
                insertStep = insertStep.values(
                        batchId,
                        item.nipam(),
                        item.potongan()
                );
            }
            totalInserted += insertStep.execute();
        }

        return totalInserted;
    }
}
