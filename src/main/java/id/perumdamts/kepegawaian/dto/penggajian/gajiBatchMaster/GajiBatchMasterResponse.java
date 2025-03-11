package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import lombok.Data;

@Data
public class GajiBatchMasterResponse {
    private Long id;
    private String gajiBatchRootId;
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
    private Double penghasilanBersih2;
    private Double pembulatan;
    private Double pembulatan2;
    private Double penghasilanBersihFinal;
    private Double penghasilanBersihFinal2;
    private Double pajak;
    private Boolean isDifferent;

    public static GajiBatchMasterResponse from(GajiBatchMaster entity) {
        GajiBatchMasterResponse result = new GajiBatchMasterResponse();
        result.setId(entity.getId());
        result.setGajiBatchRootId(entity.getGajiBatchRoot().getId());
        result.setPeriode(entity.getPeriode());
        result.setNama(entity.getNama());
        result.setNipam(entity.getNipam());
        result.setPegawaiId(entity.getPegawaiId());
        result.setStatusPegawai(entity.getStatusPegawai());
        result.setJabatanId(entity.getJabatanId());
        result.setNamaJabatan(entity.getNamaJabatan());
        result.setLevelId(entity.getLevelId());
        result.setGolonganId(entity.getGolonganId());
        result.setGolongan(entity.getGolongan());
        result.setGajiProfilId(entity.getGajiProfilId());
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
        result.setPenghasilanBersih2(entity.getPenghasilanBersih2());
        result.setPembulatan(entity.getPembulatan());
        result.setPembulatan2(entity.getPembulatan2());
        result.setPenghasilanBersihFinal(entity.getPenghasilanBersihFinal());
        result.setPenghasilanBersihFinal2(entity.getPenghasilanBersihFinal2());
        result.setPajak(entity.getPajak());
        result.setOrganisasiId(entity.getOrganisasi().getId());
        result.setOrganisasiKode(entity.getOrganisasi().getKode());
        result.setNamaOrganisasi(entity.getOrganisasi().getNama());
        result.setIsDifferent(entity.getIsDifferent());
        return result;
    }
}
