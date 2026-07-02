package id.perumdamts.kepegawaian.dto.users;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends PagedRequest {
    private String nipam;
    private String nama;
    private EStatusKerja statusKerja = EStatusKerja.KARYAWAN_AKTIF;

    public Specification<Pegawai> getSpecification() {
        return SpecificationBuilder.<Pegawai>of()
                .addLike(nipam, "nipam")
                .addLike(nama, "biodata", "nama")
                .addEqual(statusKerja, "statusKerja")
                .build();
    }
}
