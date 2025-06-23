package id.perumdamts.kepegawaian.dto.cuti.kuota;

import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;

import java.util.List;

@Data
public class CutiKuotaPegawaiResponse {
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private String statusPegawai;
    private String jabatan;
    private List<CutiKuotaResponse> cutiKuota;

    public static CutiKuotaPegawaiResponse from(Pegawai pegawai, List<CutiKuota> cutiKuota) {
        List<CutiKuota> list = cutiKuota.stream()
                .filter(cuti -> cuti.getPegawai().getId().equals(pegawai.getId()))
                .toList();
        CutiKuotaPegawaiResponse response = new CutiKuotaPegawaiResponse();
        response.setPegawaiId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setNama(pegawai.getBiodata().getNama());
        response.setStatusPegawai(pegawai.getStatusPegawai().getValue());
        response.setJabatan(pegawai.getJabatan().getNama());
        response.setCutiKuota(CutiKuotaResponse.fromList(list));
        return response;
    }
}
