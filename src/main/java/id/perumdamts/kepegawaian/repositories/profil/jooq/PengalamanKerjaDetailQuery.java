package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja.PengalamanKerjaDetailJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.PengalamanKerja.PENGALAMAN_KERJA;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class PengalamanKerjaDetailQuery {
    private final DSLContext dsl;

    public Optional<PengalamanKerjaDetail> getById(Long id) {
        return dsl.select(PengalamanKerjaSelects.COLUMNS)
                .select(multiset(dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                .from(LAMPIRAN_PROFIL)
                                .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA.ordinal()))
                                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false)))
                        .as("lampiran")
                        .convertFrom(r -> r.map(mapping(LampiranRow::new))))
                .from(PENGALAMAN_KERJA)
                .leftJoin(BIODATA).on(PENGALAMAN_KERJA.BIODATA_ID.eq(BIODATA.NIK))
                .where(PENGALAMAN_KERJA.ID.eq(id))
                .and(PENGALAMAN_KERJA.IS_DELETED.eq(false))
                .fetchOptional(PengalamanKerjaDetailJooqMapper.INSTANCE);
    }
}
