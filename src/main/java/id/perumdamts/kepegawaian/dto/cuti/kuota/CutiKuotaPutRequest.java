package id.perumdamts.kepegawaian.dto.cuti.kuota;

import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

public class CutiKuotaPutRequest extends CutiKuotaPostRequest {
    public static CutiKuota toEntity(CutiKuota entity, CutiKuotaPutRequest request, Pegawai pegawai) {
        entity.setPegawai(pegawai);
        entity.setTahun(request.getTahun());
        entity.setKuota(request.getKuota());
        entity.setKuotaTambahan(request.getKuotaTambahan());
        entity.setSisaKuota(request.getSisaKuota());
        entity.setExpired(request.getExpired());
        return entity;
    }
}
