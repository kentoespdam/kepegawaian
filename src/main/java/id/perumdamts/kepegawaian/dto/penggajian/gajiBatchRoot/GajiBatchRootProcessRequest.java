package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GajiBatchRootProcessRequest {
    private String id;
    private String nama;
    private String jabatan;

    public static GajiBatchRoot reProcess(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setStatus(EProsesGaji.PENDING.value());
        entity.setDiProsesOleh(request.getNama());
        entity.setJabatanPemroses(request.getJabatan());
        return entity;
    }

    public static GajiBatchRoot verifyPhase1(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2.value());
        entity.setTanggalVerifikasiTahap1(LocalDateTime.now());
        entity.setDiVerifikasiOlehTahap1(request.getNama());
        entity.setJabatanVerifikasiTahap1(request.getJabatan());
        return entity;
    }

    public static GajiBatchRoot verifyPhase2(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setStatus(EProsesGaji.FINISHED.value());
        entity.setTanggalVerifikasiTahap2(LocalDateTime.now());
        entity.setDiVerifikasiOlehTahap2(request.getNama());
        entity.setJabatanVerifikasiTahap2(request.getJabatan());
        return entity;
    }

    public static GajiBatchRoot accept(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setTanggalPersetujuan(LocalDateTime.now());
        entity.setDiSetujuiOleh(request.getNama());
        entity.setJabatanPenyetuju(request.getJabatan());
        return entity;
    }
}
