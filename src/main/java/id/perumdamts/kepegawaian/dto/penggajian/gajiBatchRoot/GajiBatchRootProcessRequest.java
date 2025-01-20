package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GajiBatchRootProcessRequest {
    private String batchId;
    private String nama;
    private String jabatan;

    @JsonIgnore
    public String nextBatchId(String oldBatchId) {
        String[] arrString = oldBatchId.split("-");
        int urut = Integer.parseInt(arrString[1]) + 1;
        String urutString = urut < 10 ? "00" + urut : String.valueOf(urut);
        return arrString[0] + "-" + urutString;
    }

    public static GajiBatchRoot reProcess(GajiBatchRoot oldEntity, GajiBatchRootProcessRequest request) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setBatchId(request.nextBatchId(oldEntity.getBatchId()));
        entity.setPeriode(oldEntity.getPeriode());
        entity.setStatus(EProsesGaji.PENDING);
        entity.setDiProsesOleh(request.getNama());
        entity.setJabatanPemroses(request.getJabatan());
        entity.setMimeType(oldEntity.getMimeType());
        entity.setFileName(oldEntity.getFileName());
        entity.setHashedFileName(oldEntity.getHashedFileName());
        return entity;
    }

    public static GajiBatchRoot verifyPhase1(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
        entity.setTanggalVerifikasiTahap1(LocalDateTime.now());
        entity.setDiVerifikasiOlehTahap1(request.getNama());
        entity.setJabatanVerifikasiTahap1(request.getJabatan());
        return entity;
    }

    public static GajiBatchRoot verifyPhase2(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        entity.setStatus(EProsesGaji.FINISHED);
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
