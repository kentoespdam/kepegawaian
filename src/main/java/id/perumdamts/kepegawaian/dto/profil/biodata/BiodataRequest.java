package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class BiodataRequest extends CommonPageRequest {
    private String nik;
    private String nama;
    private EJenisKelamin jenisKelamin;
    private String alamat;
    private Boolean isPegawai = false;

    @JsonIgnore
    public Specification<Biodata> getSpecification() {
        return SpecificationBuilder.<Biodata>of()
                .addLike(nik, "nik")
                .addLike(nama, "nama")
                .addEqual(jenisKelamin, "jenisKelamin")
                .addLike(alamat, "alamat")
                .addEqual(isPegawai, "isPegawai")
                .build();
    }
}
