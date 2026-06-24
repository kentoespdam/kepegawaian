package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BiodataIndexQuery extends PagedRequest {
    private String nik;
    private String nama;
    private EJenisKelamin jenisKelamin;
    private String alamat;
    private Boolean isPegawai;
}
