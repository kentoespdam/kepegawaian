package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class PengalamanKerjaRequest extends CommonPageRequest {
    private String biodataId;
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;

    public Specification<PengalamanKerja> getSpecification() {
        return SpecificationBuilder.<PengalamanKerja>of()
                .addEqual(biodataId, "biodata", "nik")
                .addLike(namaPerusahaan, "namaPerusahaan")
                .addEqual(typePerusahaan, "typePerusahaan")
                .addEqual(jabatan, "jabatan")
                .addLike(lokasi, "lokasi")
                .build();
    }
}
