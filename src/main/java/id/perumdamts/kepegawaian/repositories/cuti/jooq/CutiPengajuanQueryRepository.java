package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .where(where).fetchOne(0, Long.class);
                
        int pageNumber = query.getPage() != null ? query.getPage() : 0;
        int sizeOrDefault = query.getSize() != null ? query.getSize() : 10;
        
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        var data = dsl.select(
                        CUTI_PEGAWAI.ID,
                        CUTI_PEGAWAI.PEGAWAI_ID,
                        CUTI_PEGAWAI.NIPAM,
                        CUTI_PEGAWAI.NAMA,
                        CUTI_PEGAWAI.PANGKAT_GOLONGAN,
                        CUTI_PEGAWAI.CREATED_AT,
                        CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI,
                        CUTI_PEGAWAI.APPROVAL_CUTI_STATUS,
                        CUTI_PEGAWAI.APPROVAL_LEVEL,
                        CUTI_PEGAWAI.TANGGAL_MULAI,
                        CUTI_PEGAWAI.TANGGAL_SELESAI,
                        CUTI_PEGAWAI.ALASAN,
                        CUTI_PEGAWAI.JUMLAH_HARI,
                        CUTI_PEGAWAI.JUMLAH_HARI_KERJA,
                        CUTI_PEGAWAI.IS_CLAIMED,
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.KODE.as("jab_kode"),
                        JABATAN.NAMA.as("jab_nama"),
                        jenisCuti.ID.as("jc_id"),
                        jenisCuti.NAMA.as("jc_nama"),
                        subJenisCuti.ID.as("sjc_id"),
                        subJenisCuti.NAMA.as("sjc_nama"),
                        pic.ID.as("pic_id"),
                        pic.KODE.as("pic_kode"),
                        pic.NAMA.as("pic_nama"),
                        CUTI_PEGAWAI.REF_CUTI_ID
                )
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(sizeOrDefault)
                .offset(pageNumber * sizeOrDefault)
                .fetch(record -> {
                    CutiPengajuanResponse res = mapToResponse(record);
                    if (record.get(CUTI_PEGAWAI.REF_CUTI_ID) != null) {
                        res.setRefCuti(getMiniById(record.get(CUTI_PEGAWAI.REF_CUTI_ID)));
                    }
                    return res;
                });
                
        return new PageImpl<>(data, PageRequest.of(pageNumber, sizeOrDefault), count);
    }

    public CutiPengajuanResponse getById(Long id) {
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        var record = dsl.select(
                        CUTI_PEGAWAI.ID,
                        CUTI_PEGAWAI.PEGAWAI_ID,
                        CUTI_PEGAWAI.NIPAM,
                        CUTI_PEGAWAI.NAMA,
                        CUTI_PEGAWAI.PANGKAT_GOLONGAN,
                        CUTI_PEGAWAI.CREATED_AT,
                        CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI,
                        CUTI_PEGAWAI.APPROVAL_CUTI_STATUS,
                        CUTI_PEGAWAI.APPROVAL_LEVEL,
                        CUTI_PEGAWAI.TANGGAL_MULAI,
                        CUTI_PEGAWAI.TANGGAL_SELESAI,
                        CUTI_PEGAWAI.ALASAN,
                        CUTI_PEGAWAI.JUMLAH_HARI,
                        CUTI_PEGAWAI.JUMLAH_HARI_KERJA,
                        CUTI_PEGAWAI.IS_CLAIMED,
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.KODE.as("jab_kode"),
                        JABATAN.NAMA.as("jab_nama"),
                        jenisCuti.ID.as("jc_id"),
                        jenisCuti.NAMA.as("jc_nama"),
                        subJenisCuti.ID.as("sjc_id"),
                        subJenisCuti.NAMA.as("sjc_nama"),
                        pic.ID.as("pic_id"),
                        pic.KODE.as("pic_kode"),
                        pic.NAMA.as("pic_nama"),
                        CUTI_PEGAWAI.REF_CUTI_ID
                )
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(CUTI_PEGAWAI.ID.eq(id).and(CUTI_PEGAWAI.IS_DELETED.eq(false)))
                .fetchOne();
                
        if (record == null) return null;
        
        CutiPengajuanResponse res = mapToResponse(record);
        if (record.get(CUTI_PEGAWAI.REF_CUTI_ID) != null) {
            res.setRefCuti(getMiniById(record.get(CUTI_PEGAWAI.REF_CUTI_ID)));
        }
        return res;
    }

    private CutiPengajuanMiniResponse getMiniById(Long id) {
        var jenisCuti = CUTI_JENIS.as("jc");
        var subJenisCuti = CUTI_JENIS.as("sjc");
        var pic = JABATAN.as("pic");
        
        return dsl.select(
                        CUTI_PEGAWAI.ID,
                        CUTI_PEGAWAI.PEGAWAI_ID,
                        CUTI_PEGAWAI.NIPAM,
                        CUTI_PEGAWAI.NAMA,
                        CUTI_PEGAWAI.PANGKAT_GOLONGAN,
                        CUTI_PEGAWAI.CREATED_AT,
                        CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI,
                        CUTI_PEGAWAI.APPROVAL_CUTI_STATUS,
                        CUTI_PEGAWAI.APPROVAL_LEVEL,
                        CUTI_PEGAWAI.TANGGAL_MULAI,
                        CUTI_PEGAWAI.TANGGAL_SELESAI,
                        CUTI_PEGAWAI.ALASAN,
                        CUTI_PEGAWAI.JUMLAH_HARI,
                        CUTI_PEGAWAI.JUMLAH_HARI_KERJA,
                        CUTI_PEGAWAI.IS_CLAIMED,
                        ORGANISASI.ID.as("org_id"),
                        ORGANISASI.KODE.as("org_kode"),
                        ORGANISASI.NAMA.as("org_nama"),
                        JABATAN.ID.as("jab_id"),
                        JABATAN.KODE.as("jab_kode"),
                        JABATAN.NAMA.as("jab_nama"),
                        jenisCuti.ID.as("jc_id"),
                        jenisCuti.NAMA.as("jc_nama"),
                        subJenisCuti.ID.as("sjc_id"),
                        subJenisCuti.NAMA.as("sjc_nama"),
                        pic.ID.as("pic_id"),
                        pic.KODE.as("pic_kode"),
                        pic.NAMA.as("pic_nama")
                )
                .from(CUTI_PEGAWAI)
                .leftJoin(PEGAWAI).on(CUTI_PEGAWAI.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.NIK.eq(BIODATA.NIK))
                .leftJoin(ORGANISASI).on(CUTI_PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(CUTI_PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(jenisCuti).on(CUTI_PEGAWAI.JENIS_CUTI_ID.eq(jenisCuti.ID))
                .leftJoin(subJenisCuti).on(CUTI_PEGAWAI.SUB_JENIS_CUTI_ID.eq(subJenisCuti.ID))
                .leftJoin(pic).on(CUTI_PEGAWAI.PIC_SAAT_INI_ID.eq(pic.ID))
                .where(CUTI_PEGAWAI.ID.eq(id).and(CUTI_PEGAWAI.IS_DELETED.eq(false)))
                .fetchOne(record -> mapToMiniResponse(record));
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

    private CutiPengajuanResponse mapToResponse(org.jooq.Record record) {
        CutiPengajuanResponse res = new CutiPengajuanResponse();
        mapCommonFields(record, res);
        return res;
    }

    private CutiPengajuanMiniResponse mapToMiniResponse(org.jooq.Record record) {
        CutiPengajuanMiniResponse res = new CutiPengajuanMiniResponse();
        mapCommonFields(record, res);
        return res;
    }

    private void mapCommonFields(org.jooq.Record record, CutiPengajuanMiniResponse res) {
        res.setId(record.get(CUTI_PEGAWAI.ID));
        res.setPegawaiId(record.get(CUTI_PEGAWAI.PEGAWAI_ID));
        res.setNipam(record.get(CUTI_PEGAWAI.NIPAM));
        res.setNama(record.get(CUTI_PEGAWAI.NAMA));
        res.setPangkatGolongan(record.get(CUTI_PEGAWAI.PANGKAT_GOLONGAN));
        
        if (record.get("org_id") != null) {
            OrganisasiMiniResponse org = new OrganisasiMiniResponse();
            org.setId((Long) record.get("org_id"));
            org.setKode((String) record.get("org_kode"));
            org.setNama((String) record.get("org_nama"));
            res.setOrganisasi(org);
        }
        if (record.get("jab_id") != null) {
            JabatanMiniResponse jab = new JabatanMiniResponse();
            jab.setId((Long) record.get("jab_id"));
            jab.setKode((String) record.get("jab_kode"));
            jab.setNama((String) record.get("jab_nama"));
            res.setJabatan(jab);
        }
        
        var createdAt = record.get(CUTI_PEGAWAI.CREATED_AT);
        if (createdAt != null) {
            res.setTanggalPengajuan(createdAt.toLocalDate());
        }
        
        res.setJenisPengajuanCuti(toJenisPengajuanCuti(record.get(CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI)));
        res.setApprovalCutiStatus(toApprovalCutiStatus(record.get(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS)));
        res.setApprovalLevel(record.get(CUTI_PEGAWAI.APPROVAL_LEVEL));
        
        if (record.get("jc_id") != null) {
            CutiJenisMiniResponse jc = new CutiJenisMiniResponse();
            jc.setId((Long) record.get("jc_id"));
            jc.setNama((String) record.get("jc_nama"));
            res.setJenisCuti(jc);
        }
        if (record.get("sjc_id") != null) {
            CutiJenisMiniResponse sjc = new CutiJenisMiniResponse();
            sjc.setId((Long) record.get("sjc_id"));
            sjc.setNama((String) record.get("sjc_nama"));
            res.setSubJenisCuti(sjc);
        }
        
        res.setTanggalMulai(record.get(CUTI_PEGAWAI.TANGGAL_MULAI));
        res.setTanggalSelesai(record.get(CUTI_PEGAWAI.TANGGAL_SELESAI));
        res.setAlasan(record.get(CUTI_PEGAWAI.ALASAN));
        res.setJumlahHari(record.get(CUTI_PEGAWAI.JUMLAH_HARI));
        res.setJumlahHariKerja(record.get(CUTI_PEGAWAI.JUMLAH_HARI_KERJA));
        
        if (record.get("pic_id") != null) {
            JabatanMiniResponse picJab = new JabatanMiniResponse();
            picJab.setId((Long) record.get("pic_id"));
            picJab.setKode((String) record.get("pic_kode"));
            picJab.setNama((String) record.get("pic_nama"));
            res.setPicSaatIni(picJab);
        }
        
        Byte claimedByte = record.get(CUTI_PEGAWAI.IS_CLAIMED);
        res.setIsClaimed(claimedByte != null && claimedByte != 0);
    }

    private EJenisPengajuanCuti toJenisPengajuanCuti(Byte val) {
        if (val == null) return null;
        int intVal = val.intValue();
        if (intVal >= 0 && intVal < EJenisPengajuanCuti.values().length) {
            return EJenisPengajuanCuti.values()[intVal];
        }
        return null;
    }

    private EApprovalCutiStatus toApprovalCutiStatus(Byte val) {
        if (val == null) return null;
        int intVal = val.intValue();
        if (intVal >= 0 && intVal < EApprovalCutiStatus.values().length) {
            return EApprovalCutiStatus.values()[intVal];
        }
        return null;
    }
}
