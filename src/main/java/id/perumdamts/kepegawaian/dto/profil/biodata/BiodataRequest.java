package id.perumdamts.kepegawaian.dto.profil.biodata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

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
        Specification<Biodata> nikSpec = Objects.isNull(nik) ? null : (root, query, cb) -> cb.equal(root.get("nik"), nik);
        Specification<Biodata> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<Biodata> jenisKelaminSpec = Objects.isNull(jenisKelamin) ? null : (root, query, cb) -> cb.equal(root.get("jenisKelamin"), jenisKelamin);
        Specification<Biodata> alamatSpec = Objects.isNull(alamat) ? null : (root, query, cb) -> cb.like(root.get("alamat"), "%" + alamat + "%");
        Specification<Biodata> isPegawaiSpec = (root, query, cb) -> cb.equal(root.get("isPegawai"), isPegawai);

        return Specification.where(nikSpec).and(namaSpec).and(jenisKelaminSpec).and(alamatSpec).and(isPegawaiSpec);
    }
}
