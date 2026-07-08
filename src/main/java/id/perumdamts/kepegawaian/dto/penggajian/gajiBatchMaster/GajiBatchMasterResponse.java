package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;

import java.util.Objects;

public record GajiBatchMasterResponse(
        Long id,
        String gajiBatchRootId,
        String periode,
        Long pegawaiId,
        String nipam,
        String nama,
        EStatusPegawai statusPegawai,
        Long organisasiId,
        String organisasiKode,
        String namaOrganisasi,
        Long jabatanId,
        String namaJabatan,
        Long levelId,
        Long golonganId,
        String golongan,
        Long gajiProfilId,
        String kodePajak,
        Double gajiPokok,
        Double phdp,
        EStatusKawin statusKawin,
        Integer jmlTanggungan,
        Integer jmlJiwa,
        Double penghasilanKotor,
        Double totalPotongan,
        Double totalAddTambahan,
        Double totalAddPotongan,
        Double penghasilanBersih,
        Double penghasilanBersih2,
        Double pembulatan,
        Double pembulatan2,
        Double penghasilanBersihFinal,
        Double penghasilanBersihFinal2,
        Double pajak,
        Boolean isDifferent
) {
    public static GajiBatchMasterResponse from(GajiBatchMaster entity) {
        return new GajiBatchMasterResponse(
                entity.getId(),
                entity.getGajiBatchRoot().getId(),
                entity.getPeriode(),
                entity.getPegawaiId(),
                entity.getNipam(),
                entity.getNama(),
                entity.getStatusPegawai(),
                Objects.nonNull(entity.getOrganisasi()) ? entity.getOrganisasi().getId() : null,
                Objects.nonNull(entity.getOrganisasi()) ? entity.getOrganisasi().getKode() : null,
                Objects.nonNull(entity.getOrganisasi()) ? entity.getOrganisasi().getNama() : null,
                entity.getJabatanId(),
                entity.getNamaJabatan(),
                entity.getLevelId(),
                entity.getGolonganId(),
                entity.getGolongan(),
                entity.getGajiProfilId(),
                entity.getKodePajak(),
                entity.getGajiPokok(),
                entity.getPhdp(),
                entity.getStatusKawin(),
                entity.getJmlTanggungan(),
                entity.getJmlJiwa(),
                entity.getPenghasilanKotor(),
                entity.getTotalPotongan(),
                entity.getTotalAddTambahan(),
                entity.getTotalAddPotongan(),
                entity.getPenghasilanBersih(),
                entity.getPenghasilanBersih2(),
                entity.getPembulatan(),
                entity.getPembulatan2(),
                entity.getPenghasilanBersihFinal(),
                entity.getPenghasilanBersihFinal2(),
                entity.getPajak(),
                entity.getIsDifferent()
        );
    }
}
