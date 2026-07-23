package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.commons.RefMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseSession;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

/**
 * Query read-tier Session: paling ramping. Hanya JOIN biodata (nama+nik),
 * organisasi, jabatan. Tanpa multiset, tanpa LEVEL, tanpa gaji/SK.
 */
@Repository
@RequiredArgsConstructor
public class PegawaiSessionQueryRepository {

    private final DSLContext dsl;

    public Optional<PegawaiResponseSession> findSession(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return dsl.select(
                        PEGAWAI.ID.as("id"),
                        PEGAWAI.NIPAM.as("nipam"),
                        BIODATA.NIK.as("nik"),
                        BIODATA.NAMA.as("nama"),
                        JABATAN.ID.as("jabatan_id"),
                        JABATAN.NAMA.as("jabatan_nama"),
                        ORGANISASI.ID.as("organisasi_id"),
                        ORGANISASI.NAMA.as("organisasi_nama")
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(PEGAWAI.ID.eq(id).and(PEGAWAI.IS_DELETED.eq(false)))
                .fetchOptional(r -> new PegawaiResponseSession(
                        r.get(PEGAWAI.ID.as("id")),
                        r.get(PEGAWAI.NIPAM.as("nipam")),
                        r.get(BIODATA.NIK.as("nik")),
                        r.get(BIODATA.NAMA.as("nama")),
                        ref(r.get(JABATAN.ID.as("jabatan_id")), r.get(JABATAN.NAMA.as("jabatan_nama"))),
                        ref(r.get(ORGANISASI.ID.as("organisasi_id")), r.get(ORGANISASI.NAMA.as("organisasi_nama")))
                ));
    }

    private static RefMiniResponse ref(Long id, String nama) {
        return id == null ? null : new RefMiniResponse(id, nama);
    }
}
