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
        RiwayatMutasiQuery query = new RiwayatMutasiQuery();
        query.setId(record.get(RIWAYAT_MUTASI.ID));
        query.setNipam(record.get(RIWAYAT_MUTASI.NIPAM));
        query.setNama(record.get(RIWAYAT_MUTASI.NAMA));
        Byte jmByte = record.get(RIWAYAT_MUTASI.JENIS_MUTASI);
        if (jmByte != null) {
            query.setJenisMutasi(EJenisMutasi.values()[jmByte.intValue()]);
        }
        query.setTmtBerlaku(record.get(RIWAYAT_MUTASI.TMT_BERLAKU));
        query.setTanggalBerakhir(record.get(RIWAYAT_MUTASI.TANGGAL_BERAKHIR));
        query.setNamaOrganisasi(record.get(RIWAYAT_MUTASI.NAMA_ORGANISASI));
        query.setNamaOrganisasiLama(record.get(RIWAYAT_MUTASI.NAMA_ORGANISASI_LAMA));
        query.setNamaJabatan(record.get(RIWAYAT_MUTASI.NAMA_JABATAN));
        query.setNamaJabatanLama(record.get(RIWAYAT_MUTASI.NAMA_JABATAN_LAMA));
        query.setNamaProfesi(record.get(RIWAYAT_MUTASI.NAMA_PROFESI));
        query.setNamaProfesiLama(record.get(RIWAYAT_MUTASI.NAMA_PROFESI_LAMA));
        query.setNotes(record.get(RIWAYAT_MUTASI.NOTES));

        if (record.get("gol_id") != null) {
            query.setGolongan(new GolonganResponse(
                    (Long) record.get("gol_id"),
                    (String) record.get("gol_golongan"),
                    (String) record.get("gol_pangkat")
            ));
        }
        if (record.get("gol_l_id") != null) {
            query.setGolonganLama(new GolonganResponse(
                    (Long) record.get("gol_l_id"),
                    (String) record.get("gol_l_golongan"),
                    (String) record.get("gol_l_pangkat")
            ));
        }
        if (record.get("org_id") != null) {
            OrganisasiMiniResponse org = new OrganisasiMiniResponse();
            org.setId((Long) record.get("org_id"));
            org.setNama((String) record.get("org_nama"));
            query.setOrganisasi(org);
        }
        if (record.get("org_l_id") != null) {
            OrganisasiMiniResponse orgL = new OrganisasiMiniResponse();
            orgL.setId((Long) record.get("org_l_id"));
            orgL.setNama((String) record.get("org_l_nama"));
            query.setOrganisasiLama(orgL);
        }
        if (record.get("jab_id") != null) {
            JabatanMiniResponse jab = new JabatanMiniResponse();
            jab.setId((Long) record.get("jab_id"));
            jab.setNama((String) record.get("jab_nama"));
            query.setJabatan(jab);
        }
        if (record.get("jab_l_id") != null) {
            JabatanMiniResponse jabL = new JabatanMiniResponse();
            jabL.setId((Long) record.get("jab_l_id"));
            jabL.setNama((String) record.get("jab_l_nama"));
            query.setJabatanLama(jabL);
        }
        if (record.get("prof_id") != null) {
            query.setProfesi(new ProfesiMiniResponse());
            query.getProfesi().setId((Long) record.get("prof_id"));
            query.getProfesi().setNama((String) record.get("prof_nama"));
        }
        if (record.get("prof_l_id") != null) {
            query.setProfesiLama(new ProfesiMiniResponse());
            query.getProfesiLama().setId((Long) record.get("prof_l_id"));
            query.getProfesiLama().setNama((String) record.get("prof_l_nama"));
        }

        if (record.get("sk_id") != null) {
            RiwayatSkQuery sk = new RiwayatSkQuery();
            sk.setId((Long) record.get("sk_id"));
            sk.setNipam(query.getNipam());
            sk.setNama(query.getNama());
            sk.setNomorSk((String) record.get("sk_nomor"));
            Byte skJenisByte = record.get("sk_jenis", Byte.class);
            if (skJenisByte != null) {
                sk.setJenisSk(EJenisSk.values()[skJenisByte.intValue()]);
            }
            sk.setTanggalSk(record.get("sk_tgl", LocalDate.class));
            sk.setTmtBerlaku(record.get("sk_tmt", LocalDate.class));
            sk.setGajiPokok(record.get("sk_gaji", Double.class));
            sk.setMkgTahun(record.get("sk_mkg_t", Integer.class));
            sk.setMkgBulan(record.get("sk_mkg_b", Integer.class));
            sk.setKenaikanBerikutnya(record.get("sk_kenaikan", LocalDate.class));
            sk.setMkgbTahun(record.get("sk_mkgb_t", Integer.class));
            sk.setMkgbBulan(record.get("sk_mkgb_b", Integer.class));
            sk.setUpdateMaster(record.get("sk_upd", Boolean.class));
            sk.setNotes((String) record.get("sk_notes"));

            if (record.get("sk_gol_id") != null) {
                sk.setGolongan(new GolonganResponse(
                        (Long) record.get("sk_gol_id"),
                        (String) record.get("sk_gol_golongan"),
                        (String) record.get("sk_gol_pangkat")
                ));
            }
            query.setSkMutasi(sk);
        }

        return query;
    }
}
