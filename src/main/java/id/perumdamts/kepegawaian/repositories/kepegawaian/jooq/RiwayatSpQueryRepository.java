package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSp.RIWAYAT_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

@Repository
@RequiredArgsConstructor
public class RiwayatSpQueryRepository {
    private final DSLContext dsl;

    public Page<RiwayatSpQuery> pageQuery(Long pegawaiId, RiwayatSpRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(),
                allowedSorts(), RIWAYAT_SP.ID);

        var condition = RIWAYAT_SP.IS_DELETED.eq(false)
                .and(pegawaiId != null ? RIWAYAT_SP.PEGAWAI_ID.eq(pegawaiId) : DSL.noCondition())
                .and(request.getNomorSp() != null ? RIWAYAT_SP.NOMOR_SP.likeIgnoreCase("%" + request.getNomorSp() + "%") : DSL.noCondition())
                .and(request.getJenisSpId() != null ? RIWAYAT_SP.JENIS_SP_ID.eq(request.getJenisSpId()) : DSL.noCondition());

        var count = dsl.selectCount()
                .from(RIWAYAT_SP)
                .where(condition)
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(
                        RIWAYAT_SP.ID,
                        RIWAYAT_SP.PEGAWAI_ID,
                        RIWAYAT_SP.NIPAM,
                        RIWAYAT_SP.NAMA,
                        RIWAYAT_SP.NAMA_ORGANISASI,
                        RIWAYAT_SP.NAMA_JABATAN,
                        RIWAYAT_SP.NOMOR_SP,
                        RIWAYAT_SP.TANGGAL_SP,
                        RIWAYAT_SP.SANKSI_NOTES,
                        RIWAYAT_SP.TANGGAL_EKSEKUSI_SANKSI,
                        RIWAYAT_SP.TANGGAL_MULAI,
                        RIWAYAT_SP.TANGGAL_SELESAI,
                        RIWAYAT_SP.PENANDA_TANGAN,
                        RIWAYAT_SP.JABATAN_PENANDA_TANGAN,
                        RIWAYAT_SP.FILE_NAME,
                        RIWAYAT_SP.HASHED_FILE_NAME,
                        RIWAYAT_SP.MIME_TYPE,
                        RIWAYAT_SP.NOTES,
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.NAMA.as("jab_nama"),
                        JENIS_SP.ID.as("sp_id"),
                        JENIS_SP.NAMA.as("sp_nama"),
                        JENIS_SP.KODE.as("sp_kode"),
                        SANKSI_SP.ID.as("san_id"),
                        SANKSI_SP.KODE.as("san_kode"),
                        SANKSI_SP.KETERANGAN.as("san_keterangan")
                )
                .from(RIWAYAT_SP)
                .leftJoin(ORGANISASI).on(RIWAYAT_SP.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_SP.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(JENIS_SP).on(RIWAYAT_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .leftJoin(SANKSI_SP).on(RIWAYAT_SP.SANKSI_ID.eq(SANKSI_SP.ID))
                .where(condition)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.offset())
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    public Optional<RiwayatSpQuery> getById(Long id) {
        return dsl.select(
                        RIWAYAT_SP.ID,
                        RIWAYAT_SP.PEGAWAI_ID,
                        RIWAYAT_SP.NIPAM,
                        RIWAYAT_SP.NAMA,
                        RIWAYAT_SP.NAMA_ORGANISASI,
                        RIWAYAT_SP.NAMA_JABATAN,
                        RIWAYAT_SP.NOMOR_SP,
                        RIWAYAT_SP.TANGGAL_SP,
                        RIWAYAT_SP.SANKSI_NOTES,
                        RIWAYAT_SP.TANGGAL_EKSEKUSI_SANKSI,
                        RIWAYAT_SP.TANGGAL_MULAI,
                        RIWAYAT_SP.TANGGAL_SELESAI,
                        RIWAYAT_SP.PENANDA_TANGAN,
                        RIWAYAT_SP.JABATAN_PENANDA_TANGAN,
                        RIWAYAT_SP.FILE_NAME,
                        RIWAYAT_SP.HASHED_FILE_NAME,
                        RIWAYAT_SP.MIME_TYPE,
                        RIWAYAT_SP.NOTES,
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.NAMA.as("jab_nama"),
                        JENIS_SP.ID.as("sp_id"),
                        JENIS_SP.NAMA.as("sp_nama"),
                        JENIS_SP.KODE.as("sp_kode"),
                        SANKSI_SP.ID.as("san_id"),
                        SANKSI_SP.KODE.as("san_kode"),
                        SANKSI_SP.KETERANGAN.as("san_keterangan")
                )
                .from(RIWAYAT_SP)
                .leftJoin(ORGANISASI).on(RIWAYAT_SP.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(RIWAYAT_SP.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(JENIS_SP).on(RIWAYAT_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .leftJoin(SANKSI_SP).on(RIWAYAT_SP.SANKSI_ID.eq(SANKSI_SP.ID))
                .where(RIWAYAT_SP.ID.eq(id))
                .and(RIWAYAT_SP.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    public Optional<HashedSpFileInfo> getHashedFileInfoById(Long id) {
        return dsl.select(RIWAYAT_SP.FILE_NAME, RIWAYAT_SP.HASHED_FILE_NAME, RIWAYAT_SP.MIME_TYPE, JENIS_SP.KODE)
                .from(RIWAYAT_SP)
                .leftJoin(JENIS_SP).on(RIWAYAT_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .where(RIWAYAT_SP.ID.eq(id))
                .and(RIWAYAT_SP.IS_DELETED.eq(false))
                .fetchOptional(record -> new HashedSpFileInfo(
                        record.get(RIWAYAT_SP.FILE_NAME),
                        record.get(RIWAYAT_SP.HASHED_FILE_NAME),
                        record.get(RIWAYAT_SP.MIME_TYPE),
                        record.get(JENIS_SP.KODE)
                ));
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tanggalSp", RIWAYAT_SP.TANGGAL_SP,
                "nomorSp", RIWAYAT_SP.NOMOR_SP
        );
    }

    private RiwayatSpQuery toQuery(Record record) {
        OrganisasiMiniResponse organisasi = record.get("org_id") != null
                ? new OrganisasiMiniResponse(
                (Long) record.get("org_id"),
                null,
                (String) record.get("org_nama"),
                null)
                : null;
        JabatanMiniResponse jabatan = record.get("jab_id") != null
                ? new JabatanMiniResponse(
                (Long) record.get("jab_id"),
                null,
                null,
                (String) record.get("jab_nama"))
                : null;
        JenisSpMiniResponse jenisSp = record.get("sp_id") != null
                ? new JenisSpMiniResponse(
                (Long) record.get("sp_id"),
                (String) record.get("sp_kode"),
                (String) record.get("sp_nama"),
                null)
                : null;
        SanksiMiniResponse sanksi = record.get("san_id") != null
                ? new SanksiMiniResponse(
                (Long) record.get("san_id"),
                (String) record.get("san_kode"),
                (String) record.get("san_keterangan"),
                null)
                : null;

        return new RiwayatSpQuery(
                record.get(RIWAYAT_SP.ID),
                record.get(RIWAYAT_SP.PEGAWAI_ID),
                record.get(RIWAYAT_SP.NIPAM),
                record.get(RIWAYAT_SP.NAMA),
                organisasi,
                record.get(RIWAYAT_SP.NAMA_ORGANISASI),
                jabatan,
                record.get(RIWAYAT_SP.NAMA_JABATAN),
                record.get(RIWAYAT_SP.NOMOR_SP),
                record.get(RIWAYAT_SP.TANGGAL_SP),
                jenisSp,
                sanksi,
                record.get(RIWAYAT_SP.SANKSI_NOTES),
                record.get(RIWAYAT_SP.TANGGAL_EKSEKUSI_SANKSI),
                record.get(RIWAYAT_SP.TANGGAL_MULAI),
                record.get(RIWAYAT_SP.TANGGAL_SELESAI),
                record.get(RIWAYAT_SP.PENANDA_TANGAN),
                record.get(RIWAYAT_SP.JABATAN_PENANDA_TANGAN),
                record.get(RIWAYAT_SP.FILE_NAME),
                record.get(RIWAYAT_SP.MIME_TYPE),
                record.get(RIWAYAT_SP.NOTES)
        );
    }

    public record HashedSpFileInfo(String fileName, String hashedFileName, String mimeType, String jenisSpKode) {}
}
