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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSp.RIWAYAT_SP;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
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
                .fetchOne(0, Long.class);

        int pageNum = Objects.requireNonNullElse(request.getPage(), 0);
        int pageSize = Objects.requireNonNullElse(request.getSize(), 10);

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
                .limit(pageSize)
                .offset(pageNum * pageSize)
                .fetch(this::toQuery);

        return new PageImpl<>(data, PageRequest.of(pageNum, pageSize), count);
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
        RiwayatSpQuery query = new RiwayatSpQuery();
        query.setId(record.get(RIWAYAT_SP.ID));
        query.setPegawaiId(record.get(RIWAYAT_SP.PEGAWAI_ID));
        query.setNipam(record.get(RIWAYAT_SP.NIPAM));
        query.setNama(record.get(RIWAYAT_SP.NAMA));
        query.setNamaOrganisasi(record.get(RIWAYAT_SP.NAMA_ORGANISASI));
        query.setNamaJabatan(record.get(RIWAYAT_SP.NAMA_JABATAN));
        query.setNomorSp(record.get(RIWAYAT_SP.NOMOR_SP));
        query.setTanggalSp(record.get(RIWAYAT_SP.TANGGAL_SP));
        query.setSanksiNotes(record.get(RIWAYAT_SP.SANKSI_NOTES));
        query.setTanggalEksekusiSanksi(record.get(RIWAYAT_SP.TANGGAL_EKSEKUSI_SANKSI));
        query.setTanggalMulai(record.get(RIWAYAT_SP.TANGGAL_MULAI));
        query.setTanggalSelesai(record.get(RIWAYAT_SP.TANGGAL_SELESAI));
        query.setPenandaTangan(record.get(RIWAYAT_SP.PENANDA_TANGAN));
        query.setJabatanPenandaTangan(record.get(RIWAYAT_SP.JABATAN_PENANDA_TANGAN));
        query.setFileName(record.get(RIWAYAT_SP.FILE_NAME));
        query.setMimeType(record.get(RIWAYAT_SP.MIME_TYPE));
        query.setNotes(record.get(RIWAYAT_SP.NOTES));

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
        if (record.get("sp_id") != null) {
            JenisSpMiniResponse jsp = new JenisSpMiniResponse();
            jsp.setId((Long) record.get("sp_id"));
            jsp.setNama((String) record.get("sp_nama"));
            String spKode = (String) record.get("sp_kode");
            if (spKode != null) {
                jsp.setKode(spKode);
            }
            query.setJenisSp(jsp);
        }
        if (record.get("san_id") != null) {
            SanksiMiniResponse san = new SanksiMiniResponse();
            san.setId((Long) record.get("san_id"));
            san.setKode((String) record.get("san_kode"));
            san.setKeterangan((String) record.get("san_keterangan"));
            query.setSanksi(san);
        }

        return query;
    }

    public record HashedSpFileInfo(String fileName, String hashedFileName, String mimeType, String jenisSpKode) {}
}
