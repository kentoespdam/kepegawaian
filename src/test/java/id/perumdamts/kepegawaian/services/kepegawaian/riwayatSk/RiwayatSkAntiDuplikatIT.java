package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSkRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakCommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test ADR-0034 (kepegawaian-nay): nomor SK boleh terpakai ulang
 * antar baris selama tanggal SK berbeda (kasus "jabatan PLT berakhir -> kembali
 * ke jabatan semula dengan nomor SK yang sama"), tapi baris identik persis tetap
 * terdeteksi duplikat. Sekaligus menguji delete kontrak yang kini soft-delete
 * baris SK via FK (bukan cocokkan nomor SK).
 * <p>
 * Diuji di level repository dengan insert jdbc langsung (tanpa Envers) agar
 * independen dari drift schema tabel _aud di DB dev.
 */
@SpringBootTest
@ActiveProfiles("development")
class RiwayatSkAntiDuplikatIT {

    private static final String NOMOR_SK = "SK/IT/001/2024";
    private static final LocalDate TANGGAL_ASLI = LocalDate.of(2024, 1, 10);
    private static final LocalDate TANGGAL_KEMBALI = LocalDate.of(2025, 6, 1);

    @Autowired
    private RiwayatSkRepository repository;
    @Autowired
    private RiwayatMutasiRepository mutasiRepository;
    @Autowired
    private RiwayatKontrakCommandService kontrakService;
    @Autowired
    private JdbcTemplate jdbc;

    private Long pegawaiId;

    @BeforeEach
    void setup() {
        String nipam = "IT" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        jdbc.update("INSERT INTO pegawai (nipam, status_kerja, status_pegawai, is_deleted) VALUES (?, 0, 0, 0)", nipam);
        pegawaiId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @AfterEach
    void cleanup() {
        if (pegawaiId != null) {
            jdbc.update("DELETE FROM riwayat_mutasi WHERE pegawai_id = ?", pegawaiId);
            jdbc.update("DELETE FROM riwayat_kontrak WHERE pegawai_id = ?", pegawaiId);
            jdbc.update("DELETE FROM riwayat_sk WHERE pegawai_id = ?", pegawaiId);
            jdbc.update("DELETE FROM pegawai WHERE id = ?", pegawaiId);
        }
    }

    private long insertSk(LocalDate tanggalSk) {
        // version=0 wajib: baris jdbc bypass Hibernate, dan @Version null -> NPE saat JPA update.
        jdbc.update("INSERT INTO riwayat_sk (pegawai_id, nomor_sk, jenis_sk, tanggal_sk, tmt_berlaku, is_deleted, version) " +
                        "VALUES (?, ?, ?, ?, ?, 0, 0)",
                pegawaiId, NOMOR_SK, EJenisSk.SK_JABATAN.ordinal(), tanggalSk, tanggalSk);
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private RiwayatSkPostRequest req(LocalDate tanggalSk) {
        RiwayatSkPostRequest r = new RiwayatSkPostRequest();
        r.setPegawaiId(pegawaiId);
        r.setNomorSk(NOMOR_SK);
        r.setJenisSk(EJenisSk.SK_JABATAN);
        r.setTanggalSk(tanggalSk);
        r.setTmtBerlaku(tanggalSk);
        return r;
    }

    @Test
    void skSpec_nomorSamaTanggalBeda_tidakTerhitungDuplikat() {
        insertSk(TANGGAL_ASLI);
        assertFalse(repository.exists(req(TANGGAL_KEMBALI).getSpecification()),
                "nomor SK sama tapi tanggal SK beda tidak boleh dianggap duplikat (kasus PLT)");
    }

    @Test
    void skSpec_nomorSamaPersis_terhitungDuplikat() {
        insertSk(TANGGAL_ASLI);
        assertTrue(repository.exists(req(TANGGAL_ASLI).getSpecification()),
                "baris (pegawai, nomorSk, jenisSk, tanggalSk) identik harus terdeteksi duplikat");
    }

    @Test
    void mutasiSpec_nomorSamaTanggalBeda_tidakTerhitungDuplikat() {
        long skId = insertSk(TANGGAL_ASLI);
        jdbc.update("INSERT INTO riwayat_mutasi (pegawai_id, riwayat_sk_id, jenis_mutasi, is_deleted, version) VALUES (?, ?, 0, 0, 0)",
                pegawaiId, skId);

        RiwayatMutasiPostRequest r = new RiwayatMutasiPostRequest();
        r.setPegawaiId(pegawaiId);
        r.setNomorSk(NOMOR_SK);
        r.setJenisSk(EJenisSk.SK_JABATAN);
        r.setTanggalSk(TANGGAL_ASLI);

        assertTrue(mutasiRepository.exists(r.getSpecificationMutasi()),
                "mutasi dengan (pegawai, nomorSk, tanggalSk) yang sudah ada harus terdeteksi duplikat");
        r.setTanggalSk(TANGGAL_KEMBALI);
        assertFalse(mutasiRepository.exists(r.getSpecificationMutasi()),
                "mutasi nomor SK sama tapi tanggal SK beda harus diterima (kasus PLT)");
    }

    @Test
    void deleteKontrak_hanyaSoftDeleteSkYangDiFk() {
        long skKontrak = insertSk(TANGGAL_ASLI);
        insertSk(TANGGAL_KEMBALI); // SK lain bernomor sama (tanggal beda) — TIDAK boleh ikut terhapus

        jdbc.update("INSERT INTO riwayat_kontrak (pegawai_id, riwayat_sk_id, nomor_kontrak, tanggal_sk, tanggal_mulai, is_deleted, version) " +
                        "VALUES (?, ?, ?, ?, ?, 0, 0)",
                pegawaiId, skKontrak, NOMOR_SK, TANGGAL_ASLI, TANGGAL_ASLI);
        long kontrakId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        kontrakService.delete(kontrakId);

        assertEquals(1, isDeleted("riwayat_sk", skKontrak),
                "SK milik kontrak yang dihapus harus ikut soft-delete");
        assertEquals(1, isDeleted("riwayat_kontrak", kontrakId),
                "kontrak harus soft-delete");
        Integer other = jdbc.queryForObject(
                "SELECT is_deleted FROM riwayat_sk WHERE pegawai_id = ? AND tanggal_sk = ?",
                Integer.class, pegawaiId, TANGGAL_KEMBALI);
        assertEquals(0, other,
                "SK lain yang kebetulan bernomor sama tidak boleh ikut terhapus");
    }

    private int isDeleted(String table, long id) {
        return jdbc.queryForObject("SELECT is_deleted FROM " + table + " WHERE id = ?", Integer.class, id);
    }
}
