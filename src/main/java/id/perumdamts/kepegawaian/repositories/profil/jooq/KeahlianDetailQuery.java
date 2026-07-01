package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.jooq.tables.Biodata;
import id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian;
import id.perumdamts.kepegawaian.mapper.profil.keahlian.KeahlianDetailJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Keahlian.KEAHLIAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static org.jooq.Records.mapping;

@Repository
@RequiredArgsConstructor
public class KeahlianDetailQuery {

    private final DSLContext dsl;

    public Optional<KeahlianDetail> getById(Long id) {
        return dsl.select(KeahlianSelects.COLUMNS)
                .select(DSL.multiset(
                                dsl.select(
                                                LAMPIRAN_PROFIL.ID,
                                                LAMPIRAN_PROFIL.FILE_NAME,
                                                LAMPIRAN_PROFIL.MIME_TYPE
                                        )
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(KEAHLIAN.ID))
                                        .and(LAMPIRAN_PROFIL.REF.eq(
                                                (byte) EJenisLampiranProfil.PROFIL_KEAHLIAN.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran")
                        .convertFrom(r -> r.map(mapping(LampiranRow::new)))
                )
                .from(KEAHLIAN)
                .leftJoin(Biodata.BIODATA)
                .on(KEAHLIAN.BIODATA_ID.eq(Biodata.BIODATA.NIK))
                .leftJoin(JenisKeahlian.JENIS_KEAHLIAN)
                .on(KEAHLIAN.JENIS_KEAHLIAN_ID.eq(JenisKeahlian.JENIS_KEAHLIAN.ID))
                .where(KEAHLIAN.ID.eq(id))
                .and(KEAHLIAN.IS_DELETED.eq(false))
                .fetch(KeahlianDetailJooqMapper.INSTANCE)
                .stream()
                .findFirst();
    }
}
