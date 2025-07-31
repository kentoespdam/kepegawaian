package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.jenis.JenisCutiResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanResponse extends CutiPengajuanMiniResponse {
    private CutiPengajuanMiniResponse refCuti;

    public static CutiPengajuanResponse from(CutiPegawai entity) {
        CutiPengajuanResponse response = new CutiPengajuanResponse();
        response.setId(entity.getId());
        response.setPegawaiId(entity.getPegawai().getId());
        response.setNipam(entity.getPegawai().getNipam());
        response.setNama(entity.getPegawai().getBiodata().getNama());
        response.setPangkatGolongan(entity.getPangkatGolongan());
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getPegawai().getOrganisasi()));
        response.setJabatan(JabatanMiniResponse.from(entity.getPegawai().getJabatan()));
        response.setRefCuti(CutiPengajuanMiniResponse.from(entity.getRefCuti()));
        response.setTanggalPengajuan(entity.getCreatedAt().toLocalDate());
        response.setJenisPengajuanCuti(entity.getJenisPengajuanCuti());
        response.setApprovalCutiStatus(entity.getApprovalCutiStatus());
        response.setApprovalLevel(entity.getApprovalLevel());
        response.setJenisCuti(JenisCutiResponse.from(entity.getJenisCuti()));
        if (Objects.nonNull(entity.getSubJenisCuti()))
            response.setSubJenisCuti(JenisCutiResponse.from(entity.getSubJenisCuti()));
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setAlasan(entity.getAlasan());
        response.setJumlahHari(entity.getJumlahHari());
        response.setJumlahHariKerja(entity.getJumlahHariKerja());
        response.setPicSaatIni(JabatanMiniResponse.from(entity.getPicSaatIni()));
        response.setIsClaimed(entity.getIsClaimed());
        return response;
    }
}
