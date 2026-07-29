package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseMutasiContext;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.val;

/**
 * Query read-tier khusus form mutasi. Mengembalikan 6 field pegawai
 * (nipam, nama, golongan, organisasi, jabatan, profesi) lengkap
 * dengan id+nama untuk tiap referensi. Golongan.nama diformat
 * "{golongan} - {pangkat}" siap tampil.
 */
@Repository
@RequiredArgsConstructor
public class PegawaiMutasiContextQueryRepository {

    private final DSLContext dsl;

    public Optional<PegawaiResponseMutasiContext> findMutasiContext(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return dsl.select(
                        PEGAWAI.ID.as("id"),
                        PEGAWAI.NIPAM.as("nipam"),
                        BIODATA.NAMA.as("nama"),
                        GOLONGAN.ID.as("golongan_id"),
                        concat(GOLONGAN.GOLONGAN_, val(" - "), GOLONGAN.PANGKAT).as("golongan_nama"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.NAMA.as("organisasi_nama"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        PROFESI.ID.as("profesi_id"),
                        PROFESI.NAMA.as("profesi_nama")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(GOLONGAN).on(PEGAWAI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(PEGAWAI.PROFESI_ID.eq(PROFESI.ID))
                .where(PEGAWAI.ID.eq(id).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetchOptional(r -> new PegawaiResponseMutasiContext(
                        r.get(PEGAWAI.ID.as("id")),
                        r.get(PEGAWAI.NIPAM.as("nipam")),
                        r.get(BIODATA.NAMA.as("nama")),
                        ref(r.get(GOLONGAN.ID.as("golongan_id")), r.get(GOLONGAN.GOLONGAN_.as("golongan_nama"))),
                        ref(r.get(ORGANISASI.ID.as("organisasi_id")), r.get(ORGANISASI.NAMA.as("organisasi_nama"))),
                        ref(r.get(JABATAN.ID.as("jabatan_id")), r.get(JABATAN.NAMA.as("jabatan_nama"))),
                        ref(r.get(PROFESI.ID.as("profesi_id")), r.get(PROFESI.NAMA.as("profesi_nama")))
                ));
    }

    private static RefMiniResponse ref(Long id, String nama) {
        return id == null ? null : new RefMiniResponse(id, nama);
    }
}
