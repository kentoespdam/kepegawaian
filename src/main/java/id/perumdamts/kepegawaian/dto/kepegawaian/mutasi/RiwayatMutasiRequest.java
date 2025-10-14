package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiRequest extends CommonPageRequest {
    private Long pegawaiId;
    private Long riwayatSkId;
    private String nomorSk;
    private EJenisMutasi jenisMutasi;
    private Long organisasiId;
    private String namaOrganisasi;
    private Long jabatanId;
    private String namaJabatan;
    private Long organisasiLamaId;
    private String namaOrganisasiLama;
    private Long jabatanLamaId;
    private String namaJabatanLama;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecification() {
        return SpecificationBuilder.<RiwayatMutasi>of()
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(riwayatSkId, "riwayatSk", "id")
                .addEqual(jenisMutasi, "jenisMutasi")
                .addEqual(nomorSk, "riwayatSk", "nomorSk")
                .addEqual(organisasiId, "organisasi", "id")
                .addEqual(namaOrganisasi, "organisasi", "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(namaJabatan, "jabatan", "nama")
                .addEqual(organisasiLamaId, "organisasiLama", "id")
                .addEqual(namaOrganisasiLama, "organisasiLama", "nama")
                .addEqual(jabatanLamaId, "jabatanLama", "id")
                .addEqual(namaJabatanLama, "jabatanLama", "nama")
                .build();
    }

}
