package id.perumdamts.kepegawaian.mapper.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

import java.util.Objects;

public final class CutiPegawaiMapper {
    private CutiPegawaiMapper() {}

    public static CutiPegawai toEntity(CutiPengajuanPostRequest request, Pegawai pegawai,
                                        CutiJenis cutiJenis, CutiJenis subJenisCuti) {
        CutiPegawai entity = new CutiPegawai();
        entity.setPegawai(pegawai);
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNipam(pegawai.getNipam());
        entity.setPangkatGolongan(pegawai.getGolongan().getGolongan() + " - " + pegawai.getGolongan().getPangkat());
        entity.setOrganisasi(pegawai.getOrganisasi());
        entity.setJabatan(pegawai.getJabatan());
        entity.setJenisPengajuanCuti(EJenisPengajuanCuti.PENGAJUAN_CUTI);
        entity.setJenisCuti(cutiJenis);
        if (Objects.nonNull(subJenisCuti))
            entity.setSubJenisCuti(subJenisCuti);
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setAlasan(request.getAlasan());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        entity.setApprovalLevel(1);
        return entity;
    }

    public static CutiPegawai updateEntity(CutiPegawai entity, CutiPengajuanPostRequest request,
                                            Pegawai pegawai, CutiJenis cutiJenis, CutiJenis subJenisCuti) {
        entity.setJenisPengajuanCuti(EJenisPengajuanCuti.PENGAJUAN_CUTI);
        entity.setJenisCuti(cutiJenis);
        if (Objects.nonNull(subJenisCuti))
            entity.setSubJenisCuti(subJenisCuti);
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setAlasan(request.getAlasan());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        entity.setApprovalLevel(1);
        entity.setPicSaatIni(pegawai.getJabatan().getParent());
        return entity;
    }

    public static CutiPegawai toEntity(CutiPegawai cutiPegawai, CutiPengajuanKlaimPostRequest request) {
        CutiPegawai entity = new CutiPegawai();
        entity.setRefCuti(cutiPegawai);
        entity.setPegawai(cutiPegawai.getPegawai());
        entity.setNipam(cutiPegawai.getNipam());
        entity.setNama(cutiPegawai.getNama());
        entity.setPangkatGolongan(cutiPegawai.getPangkatGolongan());
        entity.setOrganisasi(cutiPegawai.getOrganisasi());
        entity.setJabatan(cutiPegawai.getJabatan());
        entity.setJenisPengajuanCuti(EJenisPengajuanCuti.KLAIM_CUTI);
        entity.setJenisCuti(cutiPegawai.getJenisCuti());
        entity.setSubJenisCuti(cutiPegawai.getSubJenisCuti());
        entity.setAlasan(request.getKeterangan());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        entity.setApprovalLevel(1);
        return entity;
    }
}
