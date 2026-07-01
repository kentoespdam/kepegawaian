package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaDetail;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.keluarga.ProfilKeluargaDetailJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;
import static org.jooq.Records.mapping;

@Repository
@RequiredArgsConstructor
public class ProfilKeluargaDetailQuery {
    private final DSLContext dsl;

    public Optional<ProfilKeluargaDetail> getById(Long id) {
        return dsl.select(ProfilKeluargaSelects.COLUMNS)
                .select(
                        DSL.multiset(
                                dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                        .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_KELUARGA.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran").convertFrom(r -> r.map(mapping(LampiranRow::new)))
                )
                .from(PROFIL_KELUARGA)
                .leftJoin(BIODATA).on(PROFIL_KELUARGA.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(PROFIL_KELUARGA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PROFIL_KELUARGA.ID.eq(id))
                .fetch(ProfilKeluargaDetailJooqMapper.INSTANCE)
                .stream()
                .findFirst();
    }
}