package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PengalamanKerjaRequest extends CommonPageRequest {
    private String biodataId;
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;

    public Specification<PengalamanKerja> getSpecification() {
        Specification<PengalamanKerja> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<PengalamanKerja> namaPerusahaanSpec = Objects.isNull(namaPerusahaan) ? null :
                (root, query, cb) -> cb.like(root.get("namaPerusahaan"), "%" + namaPerusahaan + "%");
        Specification<PengalamanKerja> typePerusahaanSpec = Objects.isNull(typePerusahaan) ? null :
                (root, query, cb) -> cb.like(root.get("typePerusahaan"), "%" + typePerusahaan + "%");
        Specification<PengalamanKerja> jabatanSpec = Objects.isNull(jabatan) ? null :
                (root, query, cb) -> cb.like(root.get("jabatan"), "%" + jabatan + "%");
        Specification<PengalamanKerja> lokasiSpec = Objects.isNull(lokasi) ? null :
                (root, query, cb) -> cb.like(root.get("lokasi"), "%" + lokasi + "%");

        return Specification.where(biodataSpec).and(namaPerusahaanSpec).and(typePerusahaanSpec)
                .and(jabatanSpec).and(lokasiSpec);
    }
}
