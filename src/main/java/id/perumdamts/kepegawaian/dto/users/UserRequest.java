package id.perumdamts.kepegawaian.dto.users;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends CommonPageRequest {
    private String nipam;
    private String nama;
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;

    public Specification<Pegawai> getSpecification() {
        Specification<Pegawai> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nipam"), nipam + "%");
        Specification<Pegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nama"), "%" + nama + "%");
        Specification<Pegawai> statusKerjaSpec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusKerja"), statusKerja);
        return Specification.where(nipamSpec).and(namaSpec).and(statusKerjaSpec);
    }
}
