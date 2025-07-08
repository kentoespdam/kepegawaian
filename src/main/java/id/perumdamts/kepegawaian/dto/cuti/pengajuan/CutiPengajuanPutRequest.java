package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanPutRequest extends CutiPengajuanPostRequest{
    public static CutiPegawai toEntity(CutiPegawai entity, CutiPengajuanPostRequest request, Pegawai pegawai, CutiJenis cutiJenis, CutiJenis subJenisCuti) {
        entity.setPegawai(pegawai);
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNipam(pegawai.getNipam());
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
        entity.setPicSaatIni(pegawai.getJabatan().getParent());
        return entity;
    }
}
