package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import org.jooq.Record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PegawaiDetailRefMapper {
    private PegawaiDetailRefMapper() {}

    // ── Kartu Identitas ──────────────────────────────────────────────────────

    /**
     * Build a single KartuIdentitasMiniResponse from a multiset subquery record.
     */
    public static KartuIdentitasMiniResponse mapKartuIdentitas(Record cardRecord) {
        JenisKitasResponse jenisKartu = cardRecord.get("jenis_kartu_id", Long.class) != null
                ? new JenisKitasResponse(
                cardRecord.get("jenis_kartu_id", Long.class),
                cardRecord.get("jenis_kartu_nama", String.class))
                : null;
        return new KartuIdentitasMiniResponse(
                cardRecord.get("id", Long.class),
                jenisKartu,
                cardRecord.get("nomor_kartu", String.class)
        );
    }

    /**
     * Build a List of KartuIdentitasMiniResponse from a multiset result.
     */
    public static List<KartuIdentitasMiniResponse> mapKartuIdentitasList(
            org.jooq.Result<org.jooq.Record> cards) {
        List<KartuIdentitasMiniResponse> result = new ArrayList<>();
        if (cards != null) {
            for (var cardRecord : cards) {
                result.add(mapKartuIdentitas(cardRecord));
            }
        }
        return result;
    }

    // ── BiodataResponse ──────────────────────────────────────────────────────

    /**
     * Build BiodataResponse from the main detail record.
     */
    public static BiodataResponse buildBiodata(Record r) {
        String nik = r.get("biodata_nik", String.class);
        if (nik == null) return null;

        Byte jkByte = r.get("biodata_jenis_kelamin", Byte.class);
        Byte agByte = r.get("biodata_agama", Byte.class);
        String gdStr = r.get("biodata_golongan_darah", String.class);
        Byte skByte = r.get("biodata_status_kawin", Byte.class);

        Long jpId = r.get("jenjang_id", Long.class);
        JenjangPendidikanResponse pendidikanTerakhir = jpId != null
                ? new JenjangPendidikanResponse(
                jpId,
                r.get("jenjang_nama", String.class),
                r.get("jenjang_short_name", String.class),
                r.get("jenjang_seq", Integer.class),
                r.get("jenjang_is_statistik", Boolean.class))
                : null;

        return new BiodataResponse(
                nik,
                r.get("biodata_nama", String.class),
                jkByte != null ? EJenisKelamin.values()[jkByte] : null,
                r.get("biodata_tempat_lahir", String.class),
                r.get("biodata_tanggal_lahir", LocalDate.class),
                r.get("biodata_alamat", String.class),
                r.get("biodata_telp", String.class),
                agByte != null ? EAgama.values()[agByte] : null,
                r.get("biodata_ibu_kandung", String.class),
                pendidikanTerakhir,
                gdStr != null ? EGolonganDarah.valueOf(gdStr) : null,
                skByte != null ? EStatusKawin.values()[skByte] : null,
                r.get("biodata_foto_profil", String.class),
                r.get("biodata_notes", String.class),
                null // kartuIdentitasList will be set by the caller
        );
    }

    // ── Organisasi ───────────────────────────────────────────────────────────

    public static OrganisasiMiniResponse buildOrganisasi(Record r) {
        Long orgId = r.get("organisasi_id", Long.class);
        if (orgId == null) return null;
        return new OrganisasiMiniResponse(
                orgId,
                r.get("organisasi_kode", String.class),
                r.get("organisasi_nama", String.class),
                r.get("organisasi_short_name", String.class));
    }

    // ── Jabatan ──────────────────────────────────────────────────────────────

    public static JabatanMiniResponse buildJabatan(Record r) {
        Long jabId = r.get("jabatan_id", Long.class);
        if (jabId == null) return null;
        Long lvlId = r.get("level_id", Long.class);
        return new JabatanMiniResponse(
                jabId,
                r.get("jabatan_kode", String.class),
                lvlId != null ? new LevelResponse(lvlId, r.get("level_nama", String.class)) : null,
                r.get("jabatan_nama", String.class));
    }

    // ── Profesi ──────────────────────────────────────────────────────────────

    public static ProfesiMiniResponse buildProfesi(Record r) {
        Long profId = r.get("profesi_id", Long.class);
        if (profId == null) return null;
        return new ProfesiMiniResponse(profId, r.get("profesi_nama", String.class));
    }

    // ── Golongan ─────────────────────────────────────────────────────────────

    public static GolonganResponse buildGolongan(Record r) {
        Long golId = r.get("golongan_id", Long.class);
        if (golId == null) return null;
        return new GolonganResponse(
                golId,
                r.get("golongan_golongan", String.class),
                r.get("golongan_pangkat", String.class)
        );
    }

    // ── Grade ────────────────────────────────────────────────────────────────

    public static GradeResponse buildGrade(Record r) {
        Long grdId = r.get("grade_id", Long.class);
        if (grdId == null) return null;
        LevelResponse level = null;
        Long glvlId = r.get("grade_level_id", Long.class);
        if (glvlId != null) {
            level = new LevelResponse(glvlId, r.get("grade_level_nama", String.class));
        }
        return new GradeResponse(
                grdId,
                level,
                r.get("grade_grade", Integer.class),
                r.get("grade_tukin", Double.class)
        );
    }

    // ── GajiPendapatanNonPajak ───────────────────────────────────────────────

    public static GajiPendapatanNonPajakResponse buildKodePajak(Record r) {
        Long pajId = r.get("kode_pajak_id", Long.class);
        if (pajId == null) return null;
        return new GajiPendapatanNonPajakResponse(
                pajId,
                r.get("kode_pajak_kode", String.class),
                r.get("kode_pajak_nominal", Double.class),
                r.get("kode_pajak_notes", String.class)
        );
    }

    // ── GajiProfil ───────────────────────────────────────────────────────────

    public static GajiProfilResponse buildGajiProfil(Record r) {
        Long gpId = r.get("gaji_profil_id", Long.class);
        if (gpId == null) return null;
        return new GajiProfilResponse(
                gpId,
                r.get("gaji_profil_nama", String.class)
        );
    }

    // ── RumahDinas ───────────────────────────────────────────────────────────

    public static RumahDinasResponse buildRumahDinas(Record r) {
        Long rdId = r.get("rumah_dinas_id", Long.class);
        if (rdId == null) return null;
        return new RumahDinasResponse(
                rdId,
                r.get("rumah_dinas_nama", String.class),
                r.get("rumah_dinas_nilai", Double.class)
        );
    }
}
