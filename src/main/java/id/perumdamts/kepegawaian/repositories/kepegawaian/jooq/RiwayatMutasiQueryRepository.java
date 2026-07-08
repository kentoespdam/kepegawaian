package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatMutasi.RIWAYAT_MUTASI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;

@Repository
@RequiredArgsConstructor
public class RiwayatMutasiQueryRepository {
    private final DSLContext dsl;

    public Page<RiwayatMutasiQuery> pageQuery(RiwayatMutasiRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_MUTASI.TMT_BERLAKU);

        var condition = getFilterCondition(request);

        var count = dsl.selectCount()
                .from(RIWAYAT_MUTASI)
                .where(condition)
                .fetchOptional(0, Long.class).orElse(0L);

        var golLama = GOLONGAN.as("gol_lama");
        var orgLama = ORGANISASI.as("org_lama");
        var jabLama = JABATAN.as("jab_lama");
        var profLama = PROFESI.as("prof_lama");
        var skGol = GOLONGAN.as("sk_gol");

        var data = dsl.select(
                        RIWAYAT_MUTASI.ID,
                        RIWAYAT_MUTASI.NIPAM,
                        RIWAYAT_MUTASI.NAMA,
                        RIWAYAT_MUTASI.JENIS_MUTASI,
                        RIWAYAT_MUTASI.TMT_BERLAKU,
                        RIWAYAT_MUTASI.TANGGAL_BERAKHIR,
                        RIWAYAT_MUTASI.NAMA_ORGANISASI,
                        RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA,
                        RIWAYAT_MUTASI.NAMA_JABATAN,
                        RIWAYAT_MUTASI.NAMA_JABATAN_LAMA,
                        RIWAYAT_MUTASI.NAMA_PROFESI,
                        RIWAYAT_MUTASI.NAMA_PROFESI_LAMA,
                        RIWAYAT_MUTASI.NOTES,
                        GOLONGAN.ID.as("gol_id"), GOLONGAN.GOLONGAN_.as("gol_golongan"), GOLONGAN.PANGKAT.as("gol_pangkat"),
                        ORGANISASI.ID.as("org_id"), ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"), JABATAN.NAMA.as("jab_nama"),
                        PROFESI.ID.as("prof_id"), PROFESI.NAMA.as("prof_nama"),
                        golLama.ID.as("gol_l_id"), golLama.GOLONGAN_.as("gol_l_golongan"), golLama.PANGKAT.as("gol_l_pangkat"),
                        orgLama.ID.as("org_l_id"), orgLama.NAMA.as("org_l_nama"),
                        jabLama.ID.as("jab_l_id"), jabLama.NAMA.as("jab_l_nama"),
                        profLama.ID.as("prof_l_id"), profLama.NAMA.as("prof_l_nama"),
                        RIWAYAT_SK.ID.as("sk_id"), RIWAYAT_SK.NOMOR_SK.as("sk_nomor"), RIWAYAT_SK.JENIS_SK.as("sk_jenis"),
                        RIWAYAT_SK.TANGGAL_SK.as("sk_tgl"), RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt"),
                        RIWAYAT_SK.GAJI_POKOK.as("sk_gaji"), RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t"), RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b"),
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan"), RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t"), RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b"),
                        RIWAYAT_SK.UPDATE_MASTER.as("sk_upd"), RIWAYAT_SK.NOTES.as("sk_notes"),
                        skGol.ID.as("sk_gol_id"), skGol.GOLONGAN_.as("sk_gol_golongan"), skGol.PANGKAT.as("sk_gol_pangkat")
                )
                .from(RIWAYAT_MUTASI)
                .leftJoin(GOLONGAN).on(RIWAYAT_MUTASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_MUTASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_MUTASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(RIWAYAT_MUTASI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(golLama).on(RIWAYAT_MUTASI.GOLONGAN_LAMA_ID.eq(golLama.ID))
                .leftJoin(orgLama).on(RIWAYAT_MUTASI.ORGANISASI_LAMA_ID.eq(orgLama.ID))
                .leftJoin(jabLama).on(RIWAYAT_MUTASI.JABATAN_LAMA_ID.eq(jabLama.ID))
                .leftJoin(profLama).on(RIWAYAT_MUTASI.PROFESI_LAMA_ID.eq(profLama.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_MUTASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(skGol).on(RIWAYAT_SK.GOLONGAN_ID.eq(skGol.ID))
                .where(condition)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    public Optional<RiwayatMutasiQuery> getById(Long id) {
        var golLama = GOLONGAN.as("gol_lama");
        var orgLama = ORGANISASI.as("org_lama");
        var jabLama = JABATAN.as("jab_lama");
        var profLama = PROFESI.as("prof_lama");
        var skGol = GOLONGAN.as("sk_gol");

        return dsl.select(
                        RIWAYAT_MUTASI.ID,
                        RIWAYAT_MUTASI.NIPAM,
                        RIWAYAT_MUTASI.NAMA,
                        RIWAYAT_MUTASI.JENIS_MUTASI,
                        RIWAYAT_MUTASI.TMT_BERLAKU,
                        RIWAYAT_MUTASI.TANGGAL_BERAKHIR,
                        RIWAYAT_MUTASI.NAMA_ORGANISASI,
                        RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA,
                        RIWAYAT_MUTASI.NAMA_JABATAN,
                        RIWAYAT_MUTASI.NAMA_JABATAN_LAMA,
                        RIWAYAT_MUTASI.NAMA_PROFESI,
                        RIWAYAT_MUTASI.NAMA_PROFESI_LAMA,
                        RIWAYAT_MUTASI.NOTES,
                        GOLONGAN.ID.as("gol_id"), GOLONGAN.GOLONGAN_.as("gol_golongan"), GOLONGAN.PANGKAT.as("gol_pangkat"),
                        ORGANISASI.ID.as("org_id"), ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"), JABATAN.NAMA.as("jab_nama"),
                        PROFESI.ID.as("prof_id"), PROFESI.NAMA.as("prof_nama"),
                        golLama.ID.as("gol_l_id"), golLama.GOLONGAN_.as("gol_l_golongan"), golLama.PANGKAT.as("gol_l_pangkat"),
                        orgLama.ID.as("org_l_id"), orgLama.NAMA.as("org_l_nama"),
                        jabLama.ID.as("jab_l_id"), jabLama.NAMA.as("jab_l_nama"),
                        profLama.ID.as("prof_l_id"), profLama.NAMA.as("prof_l_nama"),
                        RIWAYAT_SK.ID.as("sk_id"), RIWAYAT_SK.NOMOR_SK.as("sk_nomor"), RIWAYAT_SK.JENIS_SK.as("sk_jenis"),
                        RIWAYAT_SK.TANGGAL_SK.as("sk_tgl"), RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt"),
                        RIWAYAT_SK.GAJI_POKOK.as("sk_gaji"), RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t"), RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b"),
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan"), RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t"), RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b"),
                        RIWAYAT_SK.UPDATE_MASTER.as("sk_upd"), RIWAYAT_SK.NOTES.as("sk_notes"),
                        skGol.ID.as("sk_gol_id"), skGol.GOLONGAN_.as("sk_gol_golongan"), skGol.PANGKAT.as("sk_gol_pangkat")
                )
                .from(RIWAYAT_MUTASI)
                .leftJoin(GOLONGAN).on(RIWAYAT_MUTASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_MUTASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_MUTASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(PROFESI).on(RIWAYAT_MUTASI.PROFESI_ID.eq(PROFESI.ID))
                .leftJoin(golLama).on(RIWAYAT_MUTASI.GOLONGAN_LAMA_ID.eq(golLama.ID))
                .leftJoin(orgLama).on(RIWAYAT_MUTASI.ORGANISASI_LAMA_ID.eq(orgLama.ID))
                .leftJoin(jabLama).on(RIWAYAT_MUTASI.JABATAN_LAMA_ID.eq(jabLama.ID))
                .leftJoin(profLama).on(RIWAYAT_MUTASI.PROFESI_LAMA_ID.eq(profLama.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_MUTASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(skGol).on(RIWAYAT_SK.GOLONGAN_ID.eq(skGol.ID))
                .where(RIWAYAT_MUTASI.ID.eq(id))
                .and(RIWAYAT_MUTASI.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tmtBerlaku", RIWAYAT_MUTASI.TMT_BERLAKU
        );
    }

    private org.jooq.Condition getFilterCondition(RiwayatMutasiRequest request) {
        var condition = RIWAYAT_MUTASI.IS_DELETED.eq(false);
        if (request.getPegawaiId() != null) {
            condition = condition.and(RIWAYAT_MUTASI.PEGAWAI_ID.eq(request.getPegawaiId()));
        }
        if (request.getJenisMutasi() != null) {
            condition = condition.and(RIWAYAT_MUTASI.JENIS_MUTASI.eq((byte) request.getJenisMutasi().ordinal()));
        }
        return condition;
    }

    private RiwayatMutasiQuery toQuery(Record record) {
        Byte jmByte = record.get(RIWAYAT_MUTASI.JENIS_MUTASI);
        EJenisMutasi jenisMutasi = jmByte != null ? EJenisMutasi.values()[jmByte.intValue()] : null;

        GolonganResponse golongan = record.get("gol_id") != null
                ? new GolonganResponse(
                (Long) record.get("gol_id"),
                (String) record.get("gol_golongan"),
                (String) record.get("gol_pangkat"))
                : null;
        GolonganResponse golonganLama = record.get("gol_l_id") != null
                ? new GolonganResponse(
                (Long) record.get("gol_l_id"),
                (String) record.get("gol_l_golongan"),
                (String) record.get("gol_l_pangkat"))
                : null;
        OrganisasiMiniResponse organisasi = record.get("org_id") != null
                ? new OrganisasiMiniResponse(
                (Long) record.get("org_id"),
                null,
                (String) record.get("org_nama"),
                null)
                : null;
        OrganisasiMiniResponse organisasiLama = record.get("org_l_id") != null
                ? new OrganisasiMiniResponse(
                (Long) record.get("org_l_id"),
                null,
                (String) record.get("org_l_nama"),
                null)
                : null;
        JabatanMiniResponse jabatan = record.get("jab_id") != null
                ? new JabatanMiniResponse(
                (Long) record.get("jab_id"),
                null,
                null,
                (String) record.get("jab_nama"))
                : null;
        JabatanMiniResponse jabatanLama = record.get("jab_l_id") != null
                ? new JabatanMiniResponse(
                (Long) record.get("jab_l_id"),
                null,
                null,
                (String) record.get("jab_l_nama"))
                : null;
        ProfesiMiniResponse profesi = record.get("prof_id") != null
                ? new ProfesiMiniResponse((Long) record.get("prof_id"), (String) record.get("prof_nama"))
                : null;
        ProfesiMiniResponse profesiLama = record.get("prof_l_id") != null
                ? new ProfesiMiniResponse((Long) record.get("prof_l_id"), (String) record.get("prof_l_nama"))
                : null;

        RiwayatSkQuery skMutasi = null;
        if (record.get("sk_id") != null) {
            Byte skJenisByte = record.get("sk_jenis", Byte.class);
            EJenisSk skJenis = skJenisByte != null ? EJenisSk.values()[skJenisByte.intValue()] : null;

            GolonganResponse skGolongan = record.get("sk_gol_id") != null
                    ? new GolonganResponse(
                    (Long) record.get("sk_gol_id"),
                    (String) record.get("sk_gol_golongan"),
                    (String) record.get("sk_gol_pangkat"))
                    : null;

            skMutasi = new RiwayatSkQuery(
                    (Long) record.get("sk_id"),
                    record.get(RIWAYAT_MUTASI.NIPAM),
                    record.get(RIWAYAT_MUTASI.NAMA),
                    (String) record.get("sk_nomor"),
                    skJenis,
                    record.get("sk_tgl", LocalDate.class),
                    record.get("sk_tmt", LocalDate.class),
                    skGolongan,
                    record.get("sk_gaji", Double.class),
                    record.get("sk_mkg_t", Integer.class),
                    record.get("sk_mkg_b", Integer.class),
                    record.get("sk_kenaikan", LocalDate.class),
                    record.get("sk_mkgb_t", Integer.class),
                    record.get("sk_mkgb_b", Integer.class),
                    record.get("sk_upd", Boolean.class),
                    (String) record.get("sk_notes")
            );
        }

        return new RiwayatMutasiQuery(
                record.get(RIWAYAT_MUTASI.ID),
                record.get(RIWAYAT_MUTASI.NIPAM),
                record.get(RIWAYAT_MUTASI.NAMA),
                skMutasi,
                jenisMutasi,
                record.get(RIWAYAT_MUTASI.TMT_BERLAKU),
                record.get(RIWAYAT_MUTASI.TANGGAL_BERAKHIR),
                golongan,
                organisasi,
                record.get(RIWAYAT_MUTASI.NAMA_ORGANISASI),
                jabatan,
                record.get(RIWAYAT_MUTASI.NAMA_JABATAN),
                profesi,
                record.get(RIWAYAT_MUTASI.NAMA_PROFESI),
                golonganLama,
                organisasiLama,
                record.get(RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA),
                jabatanLama,
                record.get(RIWAYAT_MUTASI.NAMA_JABATAN_LAMA),
                profesiLama,
                record.get(RIWAYAT_MUTASI.NAMA_PROFESI_LAMA),
                record.get(RIWAYAT_MUTASI.NOTES)
        );
    }
}
