package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;

@Data
public class PegawaiMiniResponse {
    private Long id;
    private String nipam;
    private String nama;
    private String jabatan;
    private String organisasi;

    public static PegawaiMiniResponse from(Pegawai entity) {
        PegawaiMiniResponse response = new PegawaiMiniResponse();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getBiodata().getNama());
        response.setJabatan(entity.getJabatan().getNama());
        response.setOrganisasi(entity.getOrganisasi().getNama());
        return response;
    }
}
