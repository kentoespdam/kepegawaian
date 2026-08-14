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

import java.util.Map;
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

        var data = dsl.select(RiwayatTerminasiSelects.QUERY_COLUMNS)
                .from(RIWAYAT_TERMINASI)
                .leftJoin(ALASAN_BERHENTI).on(RIWAYAT_TERMINASI.ALASAN_TERMINASI_ID.eq(ALASAN_BERHENTI.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_TERMINASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_TERMINASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(GOLONGAN).on(RIWAYAT_TERMINASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_TERMINASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(RiwayatTerminasiSelects.SK_GOL).on(RIWAYAT_SK.GOLONGAN_ID.eq(RiwayatTerminasiSelects.SK_GOL.ID))
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
        return dsl.select(RiwayatTerminasiSelects.QUERY_COLUMNS)
                .from(RIWAYAT_TERMINASI)
                .leftJoin(ALASAN_BERHENTI).on(RIWAYAT_TERMINASI.ALASAN_TERMINASI_ID.eq(ALASAN_BERHENTI.ID))
                .leftJoin(ORGANISASI).on(RIWAYAT_TERMINASI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_TERMINASI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(GOLONGAN).on(RIWAYAT_TERMINASI.GOLONGAN_ID.eq(GOLONGAN.ID))
                .leftJoin(RIWAYAT_SK).on(RIWAYAT_TERMINASI.RIWAYAT_SK_ID.eq(RIWAYAT_SK.ID))
                .leftJoin(RiwayatTerminasiSelects.SK_GOL).on(RIWAYAT_SK.GOLONGAN_ID.eq(RiwayatTerminasiSelects.SK_GOL.ID))
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
        AlasanBerhentiResponse alasanTerminasi = record.get(RiwayatTerminasiSelects.AB_ID) != null
                ? new AlasanBerhentiResponse(
                record.get(RiwayatTerminasiSelects.AB_ID),
                record.get(RiwayatTerminasiSelects.AB_NAMA),
                record.get(RiwayatTerminasiSelects.AB_NOTES))
                : null;

        OrganisasiMiniResponse organisasi = record.get(RiwayatTerminasiSelects.ORG_ID) != null
                ? new OrganisasiMiniResponse(
                record.get(RiwayatTerminasiSelects.ORG_ID),
                null,
                record.get(RiwayatTerminasiSelects.ORG_NAMA),
                null)
                : null;

        JabatanMiniResponse jabatan = record.get(RiwayatTerminasiSelects.JAB_ID) != null
                ? new JabatanMiniResponse(
                record.get(RiwayatTerminasiSelects.JAB_ID),
                null,
                null,
                record.get(RiwayatTerminasiSelects.JAB_NAMA))
                : null;

        GolonganResponse golongan = record.get(RiwayatTerminasiSelects.GOL_ID) != null
                ? new GolonganResponse(
                record.get(RiwayatTerminasiSelects.GOL_ID),
                record.get(RiwayatTerminasiSelects.GOL_GOLONGAN),
                record.get(RiwayatTerminasiSelects.GOL_PANGKAT))
                : null;

        RiwayatSkQuery skTerminasi = null;
        if (record.get(RiwayatTerminasiSelects.SK_ID) != null) {
            Byte skJenisByte = record.get(RiwayatTerminasiSelects.SK_JENIS);
            EJenisSk skJenis = skJenisByte != null ? EJenisSk.values()[skJenisByte.intValue()] : null;

            GolonganResponse skGolongan = record.get(RiwayatTerminasiSelects.SK_GOL_ID) != null
                    ? new GolonganResponse(
                    record.get(RiwayatTerminasiSelects.SK_GOL_ID),
                    record.get(RiwayatTerminasiSelects.SK_GOL_GOLONGAN),
                    record.get(RiwayatTerminasiSelects.SK_GOL_PANGKAT))
                    : null;

            skTerminasi = new RiwayatSkQuery(
                    record.get(RiwayatTerminasiSelects.SK_ID),
                    record.get(RiwayatTerminasiSelects.NIPAM),
                    record.get(RiwayatTerminasiSelects.NAMA),
                    record.get(RiwayatTerminasiSelects.SK_NOMOR),
                    skJenis,
                    record.get(RiwayatTerminasiSelects.SK_TGL),
                    record.get(RiwayatTerminasiSelects.SK_TMT),
                    skGolongan,
                    record.get(RiwayatTerminasiSelects.SK_GAJI),
                    record.get(RiwayatTerminasiSelects.SK_MKG_T),
                    record.get(RiwayatTerminasiSelects.SK_MKG_B),
                    record.get(RiwayatTerminasiSelects.SK_KENAIKAN),
                    record.get(RiwayatTerminasiSelects.SK_MKGB_T),
                    record.get(RiwayatTerminasiSelects.SK_MKGB_B),
                    record.get(RiwayatTerminasiSelects.SK_UPD),
                    record.get(RiwayatTerminasiSelects.SK_NOTES)
            );
        }

        LampiranSkQuery lampiranSkTerminasi = null;
        if (record.get(RiwayatTerminasiSelects.LAM_ID) != null) {
            lampiranSkTerminasi = new LampiranSkQuery(
                    record.get(RiwayatTerminasiSelects.LAM_ID),
                    EJenisSk.SK_PENSIUN,
                    record.get(RiwayatTerminasiSelects.SK_ID),
                    record.get(RiwayatTerminasiSelects.LAM_FILE_NAME),
                    record.get(RiwayatTerminasiSelects.LAM_MIME_TYPE),
                    record.get(RiwayatTerminasiSelects.LAM_NOTES),
                    record.get(RiwayatTerminasiSelects.LAM_DISETUJUI),
                    record.get(RiwayatTerminasiSelects.LAM_DISETUJUI_OLEH),
                    record.get(RiwayatTerminasiSelects.LAM_TGL_DISETUJUI)
            );
        }

        return new RiwayatTerminasiQuery(
                record.get(RiwayatTerminasiSelects.ID),
                alasanTerminasi,
                null, // pegawai - not fetched in query
                record.get(RiwayatTerminasiSelects.NIPAM),
                record.get(RiwayatTerminasiSelects.NAMA),
                record.get(RiwayatTerminasiSelects.NOMOR_SK),
                skTerminasi,
                lampiranSkTerminasi,
                organisasi,
                record.get(RiwayatTerminasiSelects.NAMA_ORGANISASI),
                jabatan,
                record.get(RiwayatTerminasiSelects.NAMA_JABATAN),
                golongan,
                record.get(RiwayatTerminasiSelects.NAMA_GOLONGAN),
                record.get(RiwayatTerminasiSelects.TANGGAL_TERMINASI),
                record.get(RiwayatTerminasiSelects.TAHUN_TERMINASI),
                record.get(RiwayatTerminasiSelects.MASA_KERJA),
                record.get(RiwayatTerminasiSelects.NOTES)
        );
    }
}
