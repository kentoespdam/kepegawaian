package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static id.perumdamts.kepegawaian.jooq.tables.LampiranSk.LAMPIRAN_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatTerminasi.RIWAYAT_TERMINASI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifikasi filter GET /kepegawaian/riwayat/terminasi terhadap DATA RIIL di DB dev.
 *
 * <p>Beda dengan {@link RiwayatTerminasiQueryRepositoryTest} (MockConnection): di sini
 * repository dijalankan terhadap MariaDB development sungguhan, jadi:
 * <ul>
 *   <li>setiap filter yang dideklarasikan di {@code RiwayatTerminasiRequest} diuji
 *       benar-benar menyaring (probe nilai mustahil → 0 baris);</li>
 *   <li>join {@code LAMPIRAN_SK} (1:N terhadap satu SK pensiun) tidak boleh
 *       menggandakan baris terminasi di halaman;</li>
 *   <li>filter tanggal/nama diuji memakai nilai yang diambil dari data riil, dan
 *       jumlah baris dicek silang dengan SQL count langsung.</li>
 * </ul>
 * Read-only — tidak menulis data apa pun.
 */
@SpringBootTest
@ActiveProfiles("development")
class RiwayatTerminasiMainListRealDbTest {

    @Autowired private DSLContext dsl;
    @Autowired private RiwayatTerminasiQueryRepository repo;

    private long sqlCount(org.jooq.Condition extra) {
        return dsl.selectCount().from(RIWAYAT_TERMINASI)
                .where(RIWAYAT_TERMINASI.IS_DELETED.eq(false))
                .and(extra)
                .fetchOptional(0, Long.class).orElse(0L);
    }

    @Test
    void semuaFilterMustahilMengembalikanNolBaris() {
        List<RiwayatTerminasiRequest> probes = List.of(
                probe(r -> r.setNama("__TIDAK_ADA_NAMA_XYZ__")),
                probe(r -> r.setNipam("0000000000-ZZ")),
                probe(r -> r.setPegawaiId(-1L)),
                probe(r -> r.setAlasanTerminasiId(-1L)),
                probe(r -> r.setJabatanId(-1L)),
                probe(r -> r.setOrganisasiId(-1L)),
                probe(r -> r.setGolonganId(-1L)),
                probe(r -> r.setNomorSk("__TIDAK_ADA_SK_XYZ__")),
                probe(r -> r.setTahunPensiun(1900)),
                probe(r -> r.setTanggalTerminasi(LocalDate.of(1900, 1, 1)))
        );
        for (RiwayatTerminasiRequest request : probes) {
            long total = repo.pageQuery(request).getTotalElements();
            assertEquals(0, total,
                    "filter harus benar-benar menyaring — probe nilai mustahil mengembalikan " + total + " baris");
        }
    }

    @Test
    void pageDefaultTidakMenghasilkanDuplikatTerminasi() {
        Page<RiwayatTerminasiQuery> page = repo.pageQuery(new RiwayatTerminasiRequest());
        Set<Long> ids = page.getContent().stream()
                .map(RiwayatTerminasiQuery::id)
                .collect(Collectors.toSet());
        assertEquals(page.getContent().size(), ids.size(),
                "join lampiran_sk (1:N per SK pensiun) menggandakan baris terminasi di halaman");

        // Cek langsung: berapa SK pensiun yang punya >1 lampiran aktif? Bila ada,
        // join LAMPIRAN_SK pada pageQuery pasti menggandakan baris untuk SK tsb.
        int skBerLampiranGanda = dsl.selectCount()
                .from(LAMPIRAN_SK)
                .where(LAMPIRAN_SK.IS_DELETED.eq(false))
                .groupBy(LAMPIRAN_SK.REF, LAMPIRAN_SK.REF_ID)
                .having(DSL.count().gt(1))
                .fetch().size();
        assertEquals(0, skBerLampiranGanda,
                "ada riwayat_sk pensiun dengan >1 lampiran aktif — pageQuery akan menggandakan baris");
    }

    @Test
    void filterTanggalTerminasiMengembalikanHanyaBarisTanggalTersebut() {
        // Ambil tanggal terminasi (paling umum) dari data riil, lalu filter persis.
        LocalDate tanggal = dsl.select(RIWAYAT_TERMINASI.TANGGAL_TERMINASI)
                .from(RIWAYAT_TERMINASI)
                .where(RIWAYAT_TERMINASI.IS_DELETED.eq(false))
                .and(RIWAYAT_TERMINASI.TANGGAL_TERMINASI.isNotNull())
                .groupBy(RIWAYAT_TERMINASI.TANGGAL_TERMINASI)
                .orderBy(DSL.count().desc())
                .limit(1)
                .fetchOptional(0, LocalDate.class).orElse(null);
        if (tanggal == null) {
            return; // DB dev belum punya data terminasi
        }

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setTanggalTerminasi(tanggal);

        Page<RiwayatTerminasiQuery> page = repo.pageQuery(request);
        assertEquals(sqlCount(RIWAYAT_TERMINASI.TANGGAL_TERMINASI.eq(tanggal)), page.getTotalElements());
        assertTrue(page.getContent().stream()
                        .allMatch(q -> tanggal.equals(q.tanggalTerminasi())),
                "semua baris hasil filter tanggalTerminasi harus bertanggal " + tanggal);
    }

    @Test
    void filterNamaPartialMatchTerhadapDataRiil() {
        String namaRiil = dsl.select(RIWAYAT_TERMINASI.NAMA)
                .from(RIWAYAT_TERMINASI)
                .where(RIWAYAT_TERMINASI.IS_DELETED.eq(false))
                .and(RIWAYAT_TERMINASI.NAMA.isNotNull())
                .and(RIWAYAT_TERMINASI.NAMA.ne(""))
                .limit(1)
                .fetchOptional(0, String.class).orElse(null);
        if (namaRiil == null) {
            return; // DB dev belum punya data terminasi
        }
        String fragmen = namaRiil.split("\\s+")[0];

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setNama(fragmen);

        Page<RiwayatTerminasiQuery> page = repo.pageQuery(request);
        assertEquals(sqlCount(RIWAYAT_TERMINASI.NAMA.likeIgnoreCase("%" + fragmen + "%")),
                page.getTotalElements(),
                "jumlah hasil filter nama harus sama dengan SQL count langsung");
        assertTrue(page.getContent().stream()
                        .allMatch(q -> q.nama().toLowerCase().contains(fragmen.toLowerCase())),
                "semua baris hasil filter nama harus mengandung fragmen '" + fragmen + "'");
    }

    private interface ProbeSetter {
        void apply(RiwayatTerminasiRequest request);
    }

    private static RiwayatTerminasiRequest probe(ProbeSetter setter) {
        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        setter.apply(request);
        return request;
    }
}
