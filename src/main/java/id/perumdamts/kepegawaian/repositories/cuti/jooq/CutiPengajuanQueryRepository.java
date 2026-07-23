package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.mapper.cuti.CutiPegawaiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;
import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class CutiPengajuanQueryRepository {
    private final DSLContext dsl;

    public Page<CutiPengajuanResponse> pageQuery(CutiPengajuanRequest query) {
        Condition where = baseWhere(query);
        
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_PEGAWAI.ID);
                
        var count = dsl.selectCount().from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(where).fetchOptional(0, Long.class).orElse(0L);
                
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        var data = dsl.select(CutiPegawaiSelects.fullQueryFields(jenisCuti, subJenisCuti, pic))
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(record -> CutiPegawaiJooqMapper.mapToResponse(record));
                
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public CutiPengajuanResponse getById(Long id) {
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        var record = dsl.select(CutiPegawaiSelects.fullQueryFields(jenisCuti, subJenisCuti, pic))
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(CUTI_PEGAWAI.ID.eq(id).and(CUTI_PEGAWAI.IS_DELETED.eq(false)))
                .fetchOne();
                
        if (record == null) return null;
        
        CutiPengajuanResponse res = CutiPegawaiJooqMapper.mapToResponse(record);
        if (record.get(CutiPegawaiSelects.REF_CUTI_ID) != null) {
            res = setRefCuti(res, getMiniById(record.get(CutiPegawaiSelects.REF_CUTI_ID)));
        }
        return res;
    }

    private CutiPengajuanResponse setRefCuti(CutiPengajuanResponse res, CutiPengajuanMiniResponse refCuti) {
        return new CutiPengajuanResponse(
                res.id(), res.pegawaiId(), res.nama(), res.nipam(),
                res.pangkatGolongan(), res.organisasi(), res.jabatan(),
                res.tanggalPengajuan(), res.jenisPengajuanCuti(),
                res.approvalCutiStatus(), res.approvalLevel(),
                res.jenisCuti(), res.subJenisCuti(),
                res.tanggalMulai(), res.tanggalSelesai(),
                res.alasan(), res.jumlahHari(), res.jumlahHariKerja(),
                res.picSaatIni(), res.isClaimed(),
                refCuti
        );
    }

    private CutiPengajuanMiniResponse getMiniById(Long id) {
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        return dsl.select(CutiPegawaiSelects.miniQueryFields(jenisCuti, subJenisCuti, pic))
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(CUTI_PEGAWAI.ID.eq(id).and(CUTI_PEGAWAI.IS_DELETED.eq(false)))
                .fetchOne(CutiPegawaiJooqMapper::mapToMiniResponse);
    }

    private Condition baseWhere(CutiPengajuanRequest q) {
        Condition cond = CUTI_PEGAWAI.IS_DELETED.eq(false);
        if (q.getId() != null) {
            cond = cond.and(CUTI_PEGAWAI.ID.eq(q.getId()));
        }
        if (q.getPegawaiId() != null) {
            cond = cond.and(CUTI_PEGAWAI.PEGAWAI_ID.eq(q.getPegawaiId()));
        }
        if (q.getNipam() != null) {
            cond = cond.and(PEGAWAI.NIPAM.likeIgnoreCase("%" + q.getNipam() + "%"));
        }
        if (q.getNama() != null) {
            cond = cond.and(BIODATA.NAMA.likeIgnoreCase("%" + q.getNama() + "%"));
        }
        if (q.getTahun() != null) {
            cond = cond.and(
                DSL.year(CUTI_PEGAWAI.CREATED_AT).eq(q.getTahun())
                .or(DSL.year(CUTI_PEGAWAI.TANGGAL_MULAI).eq(q.getTahun()))
            );
        }
        if (q.getJabatanId() != null) {
            cond = cond.and(CUTI_PEGAWAI.JABATAN_ID.eq(q.getJabatanId()));
        }
        if (q.getPicSaatIniId() != null) {
            cond = cond.and(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(q.getPicSaatIniId()));
        }
        if (q.getApprovalCutiStatus() != null) {
            cond = cond.and(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS.eq((byte) q.getApprovalCutiStatus().ordinal()));
        }
        if (q.getJenisPengajuanCuti() != null) {
            cond = cond.and(CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI.eq((byte) q.getJenisPengajuanCuti().ordinal()));
        }
        return cond;
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tanggalMulai", CUTI_PEGAWAI.TANGGAL_MULAI,
                "tanggalSelesai", CUTI_PEGAWAI.TANGGAL_SELESAI,
                "jumlahHariKerja", CUTI_PEGAWAI.JUMLAH_HARI_KERJA,
                "approvalCutiStatus", CUTI_PEGAWAI.APPROVAL_CUTI_STATUS
        );
    }
}
