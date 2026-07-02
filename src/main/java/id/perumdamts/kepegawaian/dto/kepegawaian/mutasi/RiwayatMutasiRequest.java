package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiRequest extends PagedRequest {
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
}
