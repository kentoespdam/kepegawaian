package id.perumdamts.kepegawaian.mapper.cuti.kuota;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPutRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

public final class CutiKuotaMapper {
    private CutiKuotaMapper() {}

    public static CutiKuota toEntity(CutiKuotaPostRequest request, Pegawai pegawai) {
        CutiKuota entity = new CutiKuota();
        entity.setPegawai(pegawai);
        entity.setTahun(request.getTahun());
        entity.setKuota(request.getKuota());
        entity.setKuotaTambahan(request.getKuotaTambahan());
        entity.setSisaKuota(request.getSisaKuota());
        entity.setExpired(request.getExpired());
        return entity;
    }

    public static void updateEntity(CutiKuota entity, CutiKuotaPutRequest request, Pegawai pegawai) {
        entity.setPegawai(pegawai);
        entity.setTahun(request.getTahun());
        entity.setKuota(request.getKuota());
        entity.setKuotaTambahan(request.getKuotaTambahan());
        entity.setSisaKuota(request.getSisaKuota());
        entity.setExpired(request.getExpired());
    }
}
