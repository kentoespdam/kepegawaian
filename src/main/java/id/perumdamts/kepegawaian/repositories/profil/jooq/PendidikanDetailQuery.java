package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class PendidikanDetailQuery {
    private final DSLContext dsl;

    public Optional<PendidikanQuery> getById(Long id) {
        return dsl.select(PendidikanSelects.COLUMNS)
                .from(PENDIDIKAN)
                .leftJoin(BIODATA).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PENDIDIKAN.ID.eq(id))
                .fetch(new PendidikanRowMapper())
                .stream()
                .findFirst();
    }
}
