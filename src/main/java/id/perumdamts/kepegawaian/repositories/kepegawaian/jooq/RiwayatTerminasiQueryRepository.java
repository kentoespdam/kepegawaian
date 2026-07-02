package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
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

import static id.perumdamts.kepegawaian.jooq.tables.AlasanBerhenti.ALASAN_BERHENTI;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranSk.LAMPIRAN_SK;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatTerminasi.RIWAYAT_TERMINASI;

@Repository
@RequiredArgsConstructor
public class RiwayatTerminasiQueryRepository {
    private final DSLContext dsl;

    public Page<RiwayatTerminasiQuery> pageQuery(RiwayatTerminasiRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_TERMINASI.TANGGAL_TERMINASI);

        var condition = getFilterCondition(request);

        var count = dsl.selectCount()
                .from(RIWAYAT_TERMINASI)
                .where(condition)
                .fetchOptional(0, Long.class).orElse(0L);

        var skGol = GOLONGAN.as("sk_gol");

        var data = dsl.select(
                        RIWAYAT_TERMINASI.ID,
                        RIWAYAT_TERMINASI.NIPAM,
                        RIWAYAT_TERMINASI.NAMA,
                        RIWAYAT_TERMINASI.NOMOR_SK,
                        RIWAYAT_TERMINASI.TANGGAL_TERMINASI,
                        RIWAYAT_TERMINASI.TAHUN_TERMINASI,
                        RIWAYAT_TERMINASI.MASA_KERJA,
                        RIWAYAT_TERMINASI.NOTES,
                        ALASAN_BERHENTI.ID.as("ab_id"), ALASAN_BERHENTI.NAMA.as("ab_nama"), ALASAN_BERHENTI.NOTES.as("ab_notes"),
                        ORGANISASI.ID.as("org_id"), ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"), JABATAN.NAMA.as("jab_nama"),
                        GOLONGAN.ID.as("gol_id"), GOLONGAN.GOLONGAN_.as("gol_golongan"), GOLONGAN.PANGKAT.as("gol_pangkat"),
                        RIWAYAT_SK.ID.as("sk_id"), RIWAYAT_SK.NOMOR_SK.as("sk_nomor"), RIWAYAT_SK.JENIS_SK.as("sk_jenis"),
                        RIWAYAT_SK.TANGGAL_SK.as("sk_tgl"), RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt"),
                        RIWAYAT_SK.GAJI_POKOK.as("sk_gaji"), RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t"), RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b"),
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan"), RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t"), RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b"),
                        RIWAYAT_SK.UPDATE_MASTER.as("sk_upd"), RIWAYAT_SK.NOTES.as("sk_notes"),
                        skGol.ID.as("sk_gol_id"), skGol.GOLONGAN_.as("sk_gol_golongan"), skGol.PANGKAT.as("sk_gol_pangkat"),
                        LAMPIRAN_SK.ID.as("lam_id"), LAMPIRAN_SK.FILE_NAME.as("lam_file_name"), LAMPIRAN_SK.MIME_TYPE.as("lam_mime_type"),
                        LAMPIRAN_SK.NOTES.as("lam_notes"), LAMPIRAN_SK.DISETUJUI.as("lam_disetujui"),
                        LAMPIRAN_SK.DISETUJUI_OLEH.as("lam_disetujui_oleh"), LAMPIRAN_SK.TANGGAL_DISETUJUI.as("lam_tgl_disetujui")
                )
                .from(RIWAYAT_TERMINASI)
                .leftJoin(ALASAN_BERHENTI).on(RIWAYAT_TERMINASI.ALASAN_TERMINASI_ID.eq(ALASAN_BERHENTI.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_TERMINASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_TERMINASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(GOLONGAN).on(RIWAYAT_TERMINASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_TERMINASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(skGol).on(RIWAYAT_SK.GOLONGAN_ID.eq(skGol.ID))
                .leftJoin(LAMPIRAN_SK).on(LAMPIRAN_SK.REF.eq((byte) EJenisSk.SK_PENSIUN.ordinal())
                        .and(LAMPIRAN_SK.REF_ID.eq(RIWAYAT_SK.ID))
                        .and(LAMPIRAN_SK.IS_DELETED.eq(false)))
                .where(condition)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    public Optional<RiwayatTerminasiQuery> getById(Long id) {
        var skGol = GOLONGAN.as("sk_gol");

        return dsl.select(
                        RIWAYAT_TERMINASI.ID,
                        RIWAYAT_TERMINASI.NIPAM,
                        RIWAYAT_TERMINASI.NAMA,
                        RIWAYAT_TERMINASI.NOMOR_SK,
                        RIWAYAT_TERMINASI.TANGGAL_TERMINASI,
                        RIWAYAT_TERMINASI.TAHUN_TERMINASI,
                        RIWAYAT_TERMINASI.MASA_KERJA,
                        RIWAYAT_TERMINASI.NOTES,
                        ALASAN_BERHENTI.ID.as("ab_id"), ALASAN_BERHENTI.NAMA.as("ab_nama"), ALASAN_BERHENTI.NOTES.as("ab_notes"),
                        ORGANISASI.ID.as("org_id"), ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"), JABATAN.NAMA.as("jab_nama"),
                        GOLONGAN.ID.as("gol_id"), GOLONGAN.GOLONGAN_.as("gol_golongan"), GOLONGAN.PANGKAT.as("gol_pangkat"),
                        RIWAYAT_SK.ID.as("sk_id"), RIWAYAT_SK.NOMOR_SK.as("sk_nomor"), RIWAYAT_SK.JENIS_SK.as("sk_jenis"),
                        RIWAYAT_SK.TANGGAL_SK.as("sk_tgl"), RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt"),
                        RIWAYAT_SK.GAJI_POKOK.as("sk_gaji"), RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t"), RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b"),
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan"), RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t"), RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b"),
                        RIWAYAT_SK.UPDATE_MASTER.as("sk_upd"), RIWAYAT_SK.NOTES.as("sk_notes"),
                        skGol.ID.as("sk_gol_id"), skGol.GOLONGAN_.as("sk_gol_golongan"), skGol.PANGKAT.as("sk_gol_pangkat"),
                        LAMPIRAN_SK.ID.as("lam_id"), LAMPIRAN_SK.FILE_NAME.as("lam_file_name"), LAMPIRAN_SK.MIME_TYPE.as("lam_mime_type"),
                        LAMPIRAN_SK.NOTES.as("lam_notes"), LAMPIRAN_SK.DISETUJUI.as("lam_disetujui"),
                        LAMPIRAN_SK.DISETUJUI_OLEH.as("lam_disetujui_oleh"), LAMPIRAN_SK.TANGGAL_DISETUJUI.as("lam_tgl_disetujui")
                )
                .from(RIWAYAT_TERMINASI)
                .leftJoin(ALASAN_BERHENTI).on(RIWAYAT_TERMINASI.ALASAN_TERMINASI_ID.eq(ALASAN_BERHENTI.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_TERMINASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_TERMINASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(GOLONGAN).on(RIWAYAT_TERMINASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_TERMINASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(skGol).on(RIWAYAT_SK.GOLONGAN_ID.eq(skGol.ID))
                .leftJoin(LAMPIRAN_SK).on(LAMPIRAN_SK.REF.eq((byte) EJenisSk.SK_PENSIUN.ordinal())
                        .and(LAMPIRAN_SK.REF_ID.eq(RIWAYAT_SK.ID))
                        .and(LAMPIRAN_SK.IS_DELETED.eq(false)))
                .where(RIWAYAT_TERMINASI.ID.eq(id))
                .and(RIWAYAT_TERMINASI.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tanggalTerminasi", RIWAYAT_TERMINASI.TANGGAL_TERMINASI
        );
    }

    private org.jooq.Condition getFilterCondition(RiwayatTerminasiRequest request) {
        var condition = RIWAYAT_TERMINASI.IS_DELETED.eq(false);
        if (request.getNipam() != null) {
            condition = condition.and(RIWAYAT_TERMINASI.NIPAM.eq(request.getNipam()));
        }
        if (request.getNomorSk() != null) {
            condition = condition.and(RIWAYAT_TERMINASI.NOMOR_SK.likeIgnoreCase("%" + request.getNomorSk() + "%"));
        }
        return condition;
    }

    private RiwayatTerminasiQuery toQuery(Record record) {
        RiwayatTerminasiQuery query = new RiwayatTerminasiQuery();
        query.setId(record.get(RIWAYAT_TERMINASI.ID));
        query.setNipam(record.get(RIWAYAT_TERMINASI.NIPAM));
        query.setNama(record.get(RIWAYAT_TERMINASI.NAMA));
        query.setNomorSk(record.get(RIWAYAT_TERMINASI.NOMOR_SK));
        query.setNamaOrganisasi(record.get(RIWAYAT_TERMINASI.NAMA_ORGANISASI));
        query.setNamaGolongan(record.get(RIWAYAT_TERMINASI.NAMA_GOLONGAN));
        query.setNamaJabatan(record.get(RIWAYAT_TERMINASI.NAMA_JABATAN));
        query.setTanggalTerminasi(record.get(RIWAYAT_TERMINASI.TANGGAL_TERMINASI));
        query.setTahunTerminasi(record.get(RIWAYAT_TERMINASI.TAHUN_TERMINASI));
        query.setMasaKerja(record.get(RIWAYAT_TERMINASI.MASA_KERJA));
        query.setNotes(record.get(RIWAYAT_TERMINASI.NOTES));

        if (record.get("ab_id") != null) {
            AlasanBerhentiResponse ab = new AlasanBerhentiResponse();
            ab.setId((Long) record.get("ab_id"));
            ab.setNama((String) record.get("ab_nama"));
            ab.setNotes((String) record.get("ab_notes"));
            query.setAlasanTerminasi(ab);
        }

        if (record.get("org_id") != null) {
            OrganisasiMiniResponse org = new OrganisasiMiniResponse();
            org.setId((Long) record.get("org_id"));
            org.setNama((String) record.get("org_nama"));
            query.setOrganisasi(org);
        }

        if (record.get("jab_id") != null) {
            JabatanMiniResponse jab = new JabatanMiniResponse();
            jab.setId((Long) record.get("jab_id"));
            jab.setNama((String) record.get("jab_nama"));
            query.setJabatan(jab);
        }

        if (record.get("gol_id") != null) {
            query.setGolongan(new GolonganResponse(
                    (Long) record.get("gol_id"),
                    (String) record.get("gol_golongan"),
                    (String) record.get("gol_pangkat")
            ));
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
            query.setSkTerminasi(sk);
        }

        if (record.get("lam_id") != null) {
            LampiranSkQuery lam = new LampiranSkQuery();
            lam.setId((Long) record.get("lam_id"));
            lam.setRef(EJenisSk.SK_PENSIUN);
            lam.setRefId((Long) record.get("sk_id"));
            lam.setFileName((String) record.get("lam_file_name"));
            lam.setMimeType((String) record.get("lam_mime_type"));
            lam.setNotes((String) record.get("lam_notes"));
            lam.setDisetujui((Boolean) record.get("lam_disetujui"));
            lam.setDisetujuiOleh((String) record.get("lam_disetujui_oleh"));
            lam.setTanggalDisetujui(record.get("lam_tgl_disetujui", java.time.LocalDateTime.class));
            query.setLampiranSkTerminasi(lam);
        }

        return query;
    }
}
