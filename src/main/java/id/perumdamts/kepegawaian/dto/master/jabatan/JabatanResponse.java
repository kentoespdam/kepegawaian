package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;

@Data
public class JabatanResponse {
    private Long id;
    private JabatanResponse jabatan;
    private OrganisasiResponse organisasi;
    private LevelResponse  level;
    private String nama;
    private PangkatResponse pangkat;
    private GolonganResponse golongan;

    public static JabatanResponse from(Jabatan entity){
        JabatanResponse response = new JabatanResponse();
        response.setId(entity.getId());
        if (entity.getJabatan() != null) {
            JabatanResponse parent = getJabatanParent(entity.getJabatan());
            response.setJabatan(parent);
        }
        response.setOrganisasi(OrganisasiResponse.from(entity.getOrganisasi()));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        response.setPangkat(PangkatResponse.from(entity.getPangkat()));
        response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        return response;
    }

    public static JabatanResponse getJabatanParent(Jabatan entity){
        JabatanResponse response = new JabatanResponse();
        response.setId(entity.getId());
        response.setOrganisasi(OrganisasiResponse.from(entity.getOrganisasi()));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        response.setPangkat(PangkatResponse.from(entity.getPangkat()));
        response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        return response;
    }
}
