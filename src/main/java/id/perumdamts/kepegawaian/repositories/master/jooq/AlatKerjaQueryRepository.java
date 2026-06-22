package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.AlatKerja.ALAT_KERJA;

@Repository
@RequiredArgsConstructor
public class AlatKerjaQueryRepository {
    private final DSLContext dsl;

    public Optional<AlatKerjaQuery> getById(Long id) {
        return dsl.select(
                        ALAT_KERJA.ID,
                        ALAT_KERJA.NAMA,
                        ALAT_KERJA.PROFESI_ID.as("profesiId"))
                .from(ALAT_KERJA)
                .where(ALAT_KERJA.ID.eq(id))
                .and(ALAT_KERJA.IS_DELETED.eq(false))
                .fetchOptionalInto(AlatKerjaQuery.class);
    }
}
