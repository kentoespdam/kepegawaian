package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse.PendidikanDashboard;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

/**
 * Dashboard biodata — multiset subqueries isolate PEGAWAI and PENDIDIKAN
 * from the main BIODATA row, eliminating JOIN fan-out.
 * <p>
 * Previous bug: flat LEFT JOINs produced &gt;1 row per NIK,
 * causing {@code "Cursor returned more than one result"} from fetchOptional().
 */
@Repository
@RequiredArgsConstructor
public class BiodataDashboardQuery {
    private final DSLContext dsl;

    // ── Multiset subqueries (correlated on BIODATA.NIK) ────────────────
    @SuppressWarnings("rawtypes")
    private final Field pegawaiMultiset = multiset(
            select(
                    PEGAWAI.EMAIL,
                    GAJI_PENDAPATAN_NON_PAJAK.KODE
            ).from(PEGAWAI)
                    .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(
                            PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID)
                                    .and(GAJI_PENDAPATAN_NON_PAJAK.IS_DELETED.eq(false)))
                    .where(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                    .and(PEGAWAI.IS_DELETED.eq(false))
    ).as("pegawai").convertFrom(r -> r.stream().findFirst().orElse(null));

    @SuppressWarnings("rawtypes")
    private final Field pendidikanMultiset = multiset(
            select(
                    JENJANG_PENDIDIKAN.NAMA.as("tingkat"),
                    PENDIDIKAN.JURUSAN,
                    PENDIDIKAN.INSTITUSI,
                    PENDIDIKAN.TAHUN_LULUS
            ).from(PENDIDIKAN)
                    .leftJoin(JENJANG_PENDIDIKAN).on(
                            PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID)
                                    .and(JENJANG_PENDIDIKAN.IS_DELETED.eq(false)))
                    .where(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK))
                    .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                    .and(PENDIDIKAN.CHANGED_STATUS.eq((byte) 0))
                    .and(PENDIDIKAN.IS_DELETED.eq(false))
    ).as("pendidikan").convertFrom(r -> r.stream().findFirst().orElse(null));

    // ── Extraction field aliases ────────────────────────────────────────
    private static final Field<String> EMAIL = field("email", String.class);
    private static final Field<String> KODE_PAJAK = field("kode", String.class);
    private static final Field<String> TINGKAT = field("tingkat", String.class);
    private static final Field<String> JURUSAN = field("jurusan", String.class);
    private static final Field<String> INSTITUSI = field("institusi", String.class);
    private static final Field<Integer> TAHUN_LULUS = field("tahun_lulus", Integer.class);

    public Optional<BiodataDashboardResponse> getByNik(String nik) {
        return dsl.select(
                        BIODATA.NIK,
                        BIODATA.NAMA,
                        BIODATA.JENIS_KELAMIN,
                        BIODATA.TEMPAT_LAHIR,
                        BIODATA.TANGGAL_LAHIR,
                        BIODATA.AGAMA,
                        BIODATA.STATUS_KAWIN,
                        BIODATA.ALAMAT,
                        BIODATA.TELP,
                        BIODATA.IBU_KANDUNG,
                        BIODATA.field("changed_status", Boolean.class),
                        pegawaiMultiset,
                        pendidikanMultiset
                ).from(BIODATA)
                .where(BIODATA.NIK.eq(nik))
                .and(BIODATA.IS_DELETED.eq(false))
                .fetchOptional()
                .map(r -> mapRow((Record) r));
    }

    @SuppressWarnings("unchecked")
    private BiodataDashboardResponse mapRow(Record r) {
        String jenisKelamin = null;
        Byte jkByte = r.get(BIODATA.JENIS_KELAMIN);
        if (jkByte != null) {
            jenisKelamin = EJenisKelamin.values()[jkByte] == EJenisKelamin.LAKI_LAKI
                    ? "Laki-Laki" : "Perempuan";
        }

        String agama = null;
        Byte agByte = r.get(BIODATA.AGAMA);
        if (agByte != null) {
            agama = EAgama.values()[agByte].toString();
        }

        String statusKawin = null;
        Byte skByte = r.get(BIODATA.STATUS_KAWIN);
        if (skByte != null) {
            statusKawin = EStatusKawin.values()[skByte].toString();
        }

        // ── Extract from PEGAWAI multiset ───────────────────────────────
        Record pegawai = (Record) r.get(pegawaiMultiset);
        String email = pegawai != null ? pegawai.get(EMAIL) : null;
        String kodePajak = pegawai != null ? pegawai.get(KODE_PAJAK) : null;

        // ── Extract from PENDIDIKAN multiset ────────────────────────────
        Record pendidikan = (Record) r.get(pendidikanMultiset);
        PendidikanDashboard detailPendidikan = null;
        if (pendidikan != null) {
            String tingkat = pendidikan.get(TINGKAT);
            String jurusan = pendidikan.get(JURUSAN);
            String institusi = pendidikan.get(INSTITUSI);
            Integer tahunLulus = pendidikan.get(TAHUN_LULUS);
            if (tingkat != null || jurusan != null || institusi != null || tahunLulus != null) {
                detailPendidikan = new PendidikanDashboard(tingkat, jurusan, institusi, tahunLulus);
            }
        }

        return new BiodataDashboardResponse(
                r.get(BIODATA.NIK),
                r.get(BIODATA.NAMA),
                jenisKelamin,
                r.get(BIODATA.TEMPAT_LAHIR),
                r.get(BIODATA.TANGGAL_LAHIR),
                agama,
                statusKawin,
                r.get(BIODATA.ALAMAT),
                r.get(BIODATA.TELP),
                email,
                kodePajak,
                r.get(BIODATA.IBU_KANDUNG),
                detailPendidikan,
                r.get("changed_status", Boolean.class)
        );
    }
}
