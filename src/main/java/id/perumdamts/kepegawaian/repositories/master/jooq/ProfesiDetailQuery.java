package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.profesi.AlatKerjaRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ApdRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.mapper.master.profesi.ProfesiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.AlatKerja.ALAT_KERJA;
import static id.perumdamts.kepegawaian.jooq.tables.Apd.APD;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class ProfesiDetailQuery {
    private final DSLContext dsl;

    public Optional<ProfesiDetail> getById(Long id) {
        return dsl.select(ProfesiSelects.PROFESI_COLUMNS)
                .select(
                        multiset(dsl.select(APD.ID, APD.NAMA)
                                .from(APD)
                                .where(APD.PROFESI_ID.eq(id))
                                .and(APD.IS_DELETED.eq(false))
                                .orderBy(APD.NAMA.asc()))
                                .as("apd_list")
                                .convertFrom(r -> r.map(mapping(ApdRow::new))),
                        multiset(dsl.select(ALAT_KERJA.ID, ALAT_KERJA.NAMA)
                                .from(ALAT_KERJA)
                                .where(ALAT_KERJA.PROFESI_ID.eq(id))
                                .and(ALAT_KERJA.IS_DELETED.eq(false))
                                .orderBy(ALAT_KERJA.NAMA.asc()))
                                .as("alat_kerja_list")
                                .convertFrom(r -> r.map(mapping(AlatKerjaRow::new))))
                .from(PROFESI)
                .leftJoin(ORGANISASI).on(PROFESI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PROFESI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(PROFESI.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GRADE).on(PROFESI.GRADE_ID.eq(GRADE.ID))
                .where(PROFESI.ID.eq(id))
                .and(PROFESI.IS_DELETED.eq(false))
                .fetchOptional(ProfesiJooqMapper::toDetail);
    }
}
