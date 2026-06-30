package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;

public final class CutiPegawaiJooqMapper {
    private CutiPegawaiJooqMapper() {}

    public static CutiPengajuanResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiPengajuanResponse res = new CutiPengajuanResponse();
        mapCommonFields(record, res);
        return res;
    }

    public static CutiPengajuanMiniResponse mapToMiniResponse(Record record) {
        if (record == null) return null;
        CutiPengajuanMiniResponse res = new CutiPengajuanMiniResponse();
        mapCommonFields(record, res);
        return res;
    }

    public static void mapCommonFields(Record record, CutiPengajuanMiniResponse res) {
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

    public static EJenisPengajuanCuti toJenisPengajuanCuti(Byte val) {
        if (val == null) return null;
        int intVal = val.intValue();
        if (intVal >= 0 && intVal < EJenisPengajuanCuti.values().length) {
            return EJenisPengajuanCuti.values()[intVal];
        }
        return null;
    }

    public static EApprovalCutiStatus toApprovalCutiStatus(Byte val) {
        if (val == null) return null;
        int intVal = val.intValue();
        if (intVal >= 0 && intVal < EApprovalCutiStatus.values().length) {
            return EApprovalCutiStatus.values()[intVal];
        }
        return null;
    }
}
