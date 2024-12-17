package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;

@Data
public class PegawaiListResponse {
    private Long id;
    private String nipam;
    private String nama;
    private OrganisasiMiniResponse organisasi;
    private JabatanMiniResponse jabatan;
    private GolonganResponse golongan;

    public static PegawaiListResponse from(Pegawai entity) {
        PegawaiListResponse response = new PegawaiListResponse();
        response.setId(entity.getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getBiodata().getNama());
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getOrganisasi()));
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        if (entity.getGolongan() != null)
            response.setGolongan(GolonganResponse.from(entity.getGolongan()));

        return response;
    }
}