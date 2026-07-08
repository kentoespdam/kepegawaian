package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import org.jooq.Record;

import java.time.LocalDate;

import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;

public final class CutiPegawaiJooqMapper {
    private CutiPegawaiJooqMapper() {}

    public static CutiPengajuanMiniResponse mapToMiniResponse(Record record) {
        return populateFromRecord(record);
    }

    public static CutiPengajuanResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiPengajuanMiniResponse mini = populateFromRecord(record);
        if (mini == null) return null;
        return new CutiPengajuanResponse(
                mini.id(),
                mini.pegawaiId(),
                mini.nama(),
                mini.nipam(),
                mini.pangkatGolongan(),
                mini.organisasi(),
                mini.jabatan(),
                mini.tanggalPengajuan(),
                mini.jenisPengajuanCuti(),
                mini.approvalCutiStatus(),
                mini.approvalLevel(),
                mini.jenisCuti(),
                mini.subJenisCuti(),
                mini.tanggalMulai(),
                mini.tanggalSelesai(),
                mini.alasan(),
                mini.jumlahHari(),
                mini.jumlahHariKerja(),
                mini.picSaatIni(),
                mini.isClaimed(),
                null
        );
    }

    public static CutiPengajuanMiniResponse populateFromRecord(Record record) {
        if (record == null) return null;

        OrganisasiMiniResponse organisasi = null;
        if (record.get("org_id") != null) {
            organisasi = new OrganisasiMiniResponse(
                    (Long) record.get("org_id"), (String) record.get("org_kode"),
                    (String) record.get("org_nama"), null);
        }

        JabatanMiniResponse jabatan = null;
        if (record.get("jab_id") != null) {
            jabatan = new JabatanMiniResponse(
                    (Long) record.get("jab_id"), (String) record.get("jab_kode"),
                    null, (String) record.get("jab_nama"));
        }

        LocalDate tanggalPengajuan = null;
        var createdAt = record.get(CUTI_PEGAWAI.CREATED_AT);
        if (createdAt != null) {
            tanggalPengajuan = createdAt.toLocalDate();
        }

        CutiJenisMiniResponse jenisCuti = null;
        if (record.get("jc_id") != null) {
            jenisCuti = new CutiJenisMiniResponse((Long) record.get("jc_id"), (String) record.get("jc_nama"));
        }

        CutiJenisMiniResponse subJenisCuti = null;
        if (record.get("sjc_id") != null) {
            subJenisCuti = new CutiJenisMiniResponse((Long) record.get("sjc_id"), (String) record.get("sjc_nama"));
        }

        JabatanMiniResponse picSaatIni = null;
        if (record.get("pic_id") != null) {
            picSaatIni = new JabatanMiniResponse(
                    (Long) record.get("pic_id"), (String) record.get("pic_kode"),
                    null, (String) record.get("pic_nama"));
        }

        Byte claimedByte = record.get(CUTI_PEGAWAI.IS_CLAIMED);
        Boolean isClaimed = claimedByte != null && claimedByte != 0;

        return new CutiPengajuanMiniResponse(
                record.get(CUTI_PEGAWAI.ID),
                record.get(CUTI_PEGAWAI.PEGAWAI_ID),
                record.get(CUTI_PEGAWAI.NAMA),
                record.get(CUTI_PEGAWAI.NIPAM),
                record.get(CUTI_PEGAWAI.PANGKAT_GOLONGAN),
                organisasi,
                jabatan,
                tanggalPengajuan,
                toJenisPengajuanCuti(record.get(CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI)),
                toApprovalCutiStatus(record.get(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS)),
                record.get(CUTI_PEGAWAI.APPROVAL_LEVEL),
                jenisCuti,
                subJenisCuti,
                record.get(CUTI_PEGAWAI.TANGGAL_MULAI),
                record.get(CUTI_PEGAWAI.TANGGAL_SELESAI),
                record.get(CUTI_PEGAWAI.ALASAN),
                record.get(CUTI_PEGAWAI.JUMLAH_HARI),
                record.get(CUTI_PEGAWAI.JUMLAH_HARI_KERJA),
                picSaatIni,
                isClaimed
        );
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
