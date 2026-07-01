package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.kartuIdentitas.KartuIdentitasDetailJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static org.jooq.Records.mapping;

@Repository
@RequiredArgsConstructor
public class KartuIdentitasDetailQuery {
    private final DSLContext dsl;

    public Optional<KartuIdentitasDetail> getById(Long id) {
        return dsl.select(KartuIdentitasSelects.COLUMNS)
                .select(
                        DSL.multiset(
                                dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                        .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.KARTU_IDENTITAS.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran").convertFrom(r -> r.map(mapping(LampiranRow::new)))
                )
                .from(KARTU_IDENTITAS)
                .leftJoin(BIODATA).on(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                .where(KARTU_IDENTITAS.ID.eq(id))
                .fetch(KartuIdentitasDetailJooqMapper.INSTANCE)
                .stream()
                .findFirst();
    }
}
