package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import lombok.Data;

@Data
public class GajiBatchMasterResponse {
    private Long id;
    private String rootBatchId;
    private String periode;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private EStatusPegawai statusPegawai;
    private Long organisasiId;
    private String organisasiKode;
    private String namaOrganisasi;
    private Long jabatanId;
    private String namaJabatan;
    private Long levelId;
    private String namaLevel;
    private Long golonganId;
    private String golongan;
    private Long gajiProfilId;
    private String kodePajak;
    private Double gajiPokok;
    private Double phdp;
    private EStatusKawin statusKawin;
    private Integer jmlTanggungan;
    private Integer jmlJiwa;
    private Double penghasilanKotor;
    private Double totalPotongan;
    private Double totalAddTambahan;
    private Double totalAddPotongan;
    private Double penghasilanBersih;
    private Double pembulatan;
    private Double penghasilanBersihFinal;
    private Boolean isDifferent;

    public static GajiBatchMasterResponse from(GajiBatchMaster entity) {
        GajiBatchMasterResponse result = new GajiBatchMasterResponse();
        result.setId(entity.getId());
        result.setRootBatchId(entity.getGajiBatchRoot().getBatchId());
        result.setPeriode(entity.getPeriode());
        result.setPegawaiId(entity.getPegawai().getId());
        result.setNipam(entity.getNipam());
        result.setNama(entity.getNama());
        result.setStatusPegawai(entity.getStatusPegawai());
        result.setOrganisasiId(entity.getOrganisasi().getId());
        result.setOrganisasiKode(entity.getOrganisasi().getKode());
        result.setNamaOrganisasi(entity.getNamaOrganisasi());
        result.setJabatanId(entity.getJabatan().getId());
        result.setNamaJabatan(entity.getNamaJabatan());
        result.setLevelId(entity.getLevelId());
        result.setGolonganId(entity.getGolonganId());
        result.setGolongan(entity.getGolongan());
        result.setGajiProfilId(entity.getGajiProfil().getId());
        result.setKodePajak(entity.getKodePajak());
        result.setGajiPokok(entity.getGajiPokok());
        result.setPhdp(entity.getPhdp());
        result.setStatusKawin(entity.getStatusKawin());
        result.setJmlTanggungan(entity.getJmlTanggungan());
        result.setJmlJiwa(entity.getJmlJiwa());
        result.setPenghasilanKotor(entity.getPenghasilanKotor());
        result.setTotalPotongan(entity.getTotalPotongan());
        result.setTotalAddTambahan(entity.getTotalAddTambahan());
        result.setTotalAddPotongan(entity.getTotalAddPotongan());
        result.setPenghasilanBersih(entity.getPenghasilanBersih());
        result.setPembulatan(entity.getPembulatan());
        result.setPenghasilanBersihFinal(entity.getPenghasilanBersihFinal());
        result.setIsDifferent(entity.getIsDifferent());
        return result;
    }
}
