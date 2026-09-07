package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiUploadResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static id.perumdamts.kepegawaian.jooq.tables.GajiKpi.GAJI_KPI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class GajiKpiBatchRepository {
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
    public GajiKpiUploadResponse batchUpsert(String periode, List<GajiKpiItem> items, String currentUser) {
        if (items == null || items.isEmpty()) {
            return new GajiKpiUploadResponse(periode, 0, 0, 0);
        }

        int chunkSize = 1000;
        int totalInserted = 0;
        int totalUpdated = 0;

        for (int i = 0; i < items.size(); i += chunkSize) {
            List<GajiKpiItem> chunk = items.subList(i, Math.min(i + chunkSize, items.size()));
            List<String> chunkNipams = chunk.stream().map(GajiKpiItem::nipam).toList();

            Set<String> existingNipams = dsl.select(GAJI_KPI.NIPAM)
                    .from(GAJI_KPI)
                    .where(GAJI_KPI.PERIODE.eq(periode))
                    .and(GAJI_KPI.NIPAM.in(chunkNipams))
                    .fetchSet(GAJI_KPI.NIPAM);

            int insertedInChunk = (int) chunk.stream()
                    .filter(item -> !existingNipams.contains(item.nipam()))
                    .count();
            int updatedInChunk = chunk.size() - insertedInChunk;

            var insertStep = dsl.insertInto(GAJI_KPI,
                    GAJI_KPI.NIPAM,
                    GAJI_KPI.PERIODE,
                    GAJI_KPI.TUNKIN,
                    GAJI_KPI.PPH21_TER,
                    GAJI_KPI.CREATED_BY,
                    GAJI_KPI.UPDATED_BY
            );
            for (var item : chunk) {
                insertStep = insertStep.values(
                        item.nipam(),
                        item.periode(),
                        item.tunkin(),
                        item.pph21Ter(),
                        currentUser,
                        currentUser
                );
            }
            insertStep.onDuplicateKeyUpdate()
                    .set(GAJI_KPI.TUNKIN, DSL.excluded(GAJI_KPI.TUNKIN))
                    .set(GAJI_KPI.PPH21_TER, DSL.excluded(GAJI_KPI.PPH21_TER))
                    .set(GAJI_KPI.UPDATED_BY, DSL.excluded(GAJI_KPI.UPDATED_BY))
                    .set(GAJI_KPI.UPDATED_AT, DSL.currentLocalDateTime())
                    .execute();

            totalInserted += insertedInChunk;
            totalUpdated += updatedInChunk;
        }

        return new GajiKpiUploadResponse(periode, items.size(), totalInserted, totalUpdated);
    }
}
