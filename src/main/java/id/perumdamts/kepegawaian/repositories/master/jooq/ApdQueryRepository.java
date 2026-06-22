package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.apd.ApdQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Apd.APD;

@Repository
@RequiredArgsConstructor
public class ApdQueryRepository {
    private final DSLContext dsl;

    public Optional<ApdQuery> getById(Long id) {
        return dsl.select(
                        APD.ID,
                        APD.NAMA,
                        APD.PROFESI_ID.as("profesiId"))
                .from(APD)
                .where(APD.ID.eq(id))
                .and(APD.IS_DELETED.eq(false))
                .fetchOptionalInto(ApdQuery.class);
    }
}
