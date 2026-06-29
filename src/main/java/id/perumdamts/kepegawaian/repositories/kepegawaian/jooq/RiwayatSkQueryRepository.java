package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;

@Repository
@RequiredArgsConstructor
public class RiwayatSkQueryRepository {
    private final DSLContext dsl;

    public Page<RiwayatSkQuery> pageQuery(RiwayatSkRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_SK.TMT_BERLAKU);

        var condition = getFilterCondition(request);

        var count = dsl.selectCount()
                .from(RIWAYAT_SK)
                .where(condition)
                .fetchOne(0, Long.class);

        int pageNum = Objects.requireNonNullElse(request.getPage(), 0);
        int pageSize = Objects.requireNonNullElse(request.getSize(), 10);

        var data = dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("gol_id"),
                        GOLONGAN.GOLONGAN_.as("gol_golongan"),
                        GOLONGAN.PANGKAT.as("gol_pangkat")
                )
                .from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(condition)
                .orderBy(sortOrder)
                .limit(pageSize)
                .offset(pageNum * pageSize)
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(pageNum, pageSize), count);
    }

    public List<RiwayatSkQuery> listQuery(RiwayatSkRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_SK.TMT_BERLAKU);

        var condition = getFilterCondition(request);

        return dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("gol_id"),
                        GOLONGAN.GOLONGAN_.as("gol_golongan"),
                        GOLONGAN.PANGKAT.as("gol_pangkat")
                )
                .from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(condition)
                .orderBy(sortOrder)
                .fetch(this::toQuery);
    }

    public Optional<RiwayatSkQuery> getById(Long id) {
        return dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("gol_id"),
                        GOLONGAN.GOLONGAN_.as("gol_golongan"),
                        GOLONGAN.PANGKAT.as("gol_pangkat")
                )
                .from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(RIWAYAT_SK.ID.eq(id))
                .and(RIWAYAT_SK.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    public List<RiwayatSkQuery> findByIds(List<Long> riwayatIds) {
        return dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("gol_id"),
                        GOLONGAN.GOLONGAN_.as("gol_golongan"),
                        GOLONGAN.PANGKAT.as("gol_pangkat")
                )
                .from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(RIWAYAT_SK.ID.in(riwayatIds))
                .and(RIWAYAT_SK.IS_DELETED.eq(false))
                .orderBy(RIWAYAT_SK.TMT_BERLAKU.desc())
                .fetch(this::toQuery);
    }

    public List<RiwayatSkQuery> findByPegawai(Long pegawaiId) {
        return dsl.select(
                        RIWAYAT_SK.ID,
                        RIWAYAT_SK.NIPAM,
                        RIWAYAT_SK.NAMA,
                        RIWAYAT_SK.NOMOR_SK,
                        RIWAYAT_SK.JENIS_SK,
                        RIWAYAT_SK.TANGGAL_SK,
                        RIWAYAT_SK.TMT_BERLAKU,
                        RIWAYAT_SK.GAJI_POKOK,
                        RIWAYAT_SK.MKG_TAHUN,
                        RIWAYAT_SK.MKG_BULAN,
                        RIWAYAT_SK.KENAIKAN_BERIKUTNYA,
                        RIWAYAT_SK.MKGB_TAHUN,
                        RIWAYAT_SK.MKGB_BULAN,
                        RIWAYAT_SK.UPDATE_MASTER,
                        RIWAYAT_SK.NOTES,
                        GOLONGAN.ID.as("gol_id"),
                        GOLONGAN.GOLONGAN_.as("gol_golongan"),
                        GOLONGAN.PANGKAT.as("gol_pangkat")
                )
                .from(RIWAYAT_SK)
                .leftJoin(GOLONGAN).on(RIWAYAT_SK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(RIWAYAT_SK.PEGAWAI_ID.eq(pegawaiId))
                .and(RIWAYAT_SK.IS_DELETED.eq(false))
                .orderBy(RIWAYAT_SK.TMT_BERLAKU.desc())
                .fetch(this::toQuery);
    }

    public Page<RiwayatSkQuery> findByPegawaiId(Long pegawaiId, RiwayatSkRequest request) {
        request.setPegawaiId(pegawaiId);
        return pageQuery(request);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tmtBerlaku", RIWAYAT_SK.TMT_BERLAKU,
                "nomorSk", RIWAYAT_SK.NOMOR_SK,
                "tanggalSk", RIWAYAT_SK.TANGGAL_SK
        );
    }

    private org.jooq.Condition getFilterCondition(RiwayatSkRequest request) {
        var condition = RIWAYAT_SK.IS_DELETED.eq(false);
        if (request.getPegawaiId() != null) {
            condition = condition.and(RIWAYAT_SK.PEGAWAI_ID.eq(request.getPegawaiId()));
        }
        if (request.getNomorSk() != null) {
            condition = condition.and(RIWAYAT_SK.NOMOR_SK.likeIgnoreCase("%" + request.getNomorSk() + "%"));
        }
        if (request.getJenisSk() != null) {
            condition = condition.and(RIWAYAT_SK.JENIS_SK.eq((byte) request.getJenisSk().ordinal()));
        }
        if (request.getGolonganId() != null) {
            condition = condition.and(RIWAYAT_SK.GOLONGAN_ID.eq(request.getGolonganId()));
        }
        return condition;
    }

    private RiwayatSkQuery toQuery(Record record) {
        RiwayatSkQuery query = new RiwayatSkQuery();
        query.setId(record.get(RIWAYAT_SK.ID));
        query.setNipam(record.get(RIWAYAT_SK.NIPAM));
        query.setNama(record.get(RIWAYAT_SK.NAMA));
        query.setNomorSk(record.get(RIWAYAT_SK.NOMOR_SK));
        Byte jenisSkByte = record.get(RIWAYAT_SK.JENIS_SK);
        if (jenisSkByte != null) {
            query.setJenisSk(EJenisSk.values()[jenisSkByte.intValue()]);
        }
        query.setTanggalSk(record.get(RIWAYAT_SK.TANGGAL_SK));
        query.setTmtBerlaku(record.get(RIWAYAT_SK.TMT_BERLAKU));
        query.setGajiPokok(record.get(RIWAYAT_SK.GAJI_POKOK));
        query.setMkgTahun(record.get(RIWAYAT_SK.MKG_TAHUN));
        query.setMkgBulan(record.get(RIWAYAT_SK.MKG_BULAN));
        query.setKenaikanBerikutnya(record.get(RIWAYAT_SK.KENAIKAN_BERIKUTNYA));
        query.setMkgbTahun(record.get(RIWAYAT_SK.MKGB_TAHUN));
        query.setMkgbBulan(record.get(RIWAYAT_SK.MKGB_BULAN));
        query.setUpdateMaster(record.get(RIWAYAT_SK.UPDATE_MASTER));
        query.setNotes(record.get(RIWAYAT_SK.NOTES));

        if (record.get("gol_id") != null) {
            query.setGolongan(new GolonganResponse(
                    (Long) record.get("gol_id"),
                    (String) record.get("gol_golongan"),
                    (String) record.get("gol_pangkat")
            ));
        }

        return query;
    }
}
