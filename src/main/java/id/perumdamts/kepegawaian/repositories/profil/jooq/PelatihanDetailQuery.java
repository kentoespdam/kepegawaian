package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.pelatihan.PelatihanDetailJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan.JENIS_PELATIHAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;
import static org.jooq.Records.mapping;

@Repository
@RequiredArgsConstructor
public class PelatihanDetailQuery {
    private final DSLContext dsl;

    public Optional<PelatihanDetail> getById(Long id) {
        return dsl.select(
                        PELATIHAN.ID,
                        PELATIHAN.BIODATA_ID,
                        PELATIHAN.JENIS_PELATIHAN_ID,
                        PELATIHAN.NAMA,
                        PELATIHAN.LEMBAGA,
                        PELATIHAN.TANGGAL_MULAI,
                        PELATIHAN.TANGGAL_SELESAI,
                        PELATIHAN.LULUS,
                        PELATIHAN.NILAI,
                        PELATIHAN.IKATAN_DINAS,
                        PELATIHAN.TANGGAL_AKHIR_IKATAN,
                        PELATIHAN.NOTES,
                        PELATIHAN.CHANGED_STATUS,
                        BIODATA.NIK.as("biodata_nik"),
                        BIODATA.NAMA.as("biodata_nama"),
                        JENIS_PELATIHAN.NAMA.as("jenis_pelatihan_nama"),
                        DSL.multiset(
                                DSL.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_PELATIHAN.ordinal())
                                                .and(LAMPIRAN_PROFIL.REF_ID.eq(PELATIHAN.ID))
                                                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false)))
                        ).as("lampiran").convertFrom(r -> r.map(mapping(LampiranRow::new))))
                .from(PELATIHAN)
                .leftJoin(BIODATA).on(PELATIHAN.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENIS_PELATIHAN).on(PELATIHAN.JENIS_PELATIHAN_ID.eq(JENIS_PELATIHAN.ID))
                .where(PELATIHAN.ID.eq(id))
                .fetch(PelatihanDetailJooqMapper.INSTANCE)
                .stream()
                .findFirst();
    }
}