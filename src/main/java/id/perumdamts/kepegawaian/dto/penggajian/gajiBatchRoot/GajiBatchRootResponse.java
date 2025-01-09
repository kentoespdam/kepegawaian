package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLogs.GajiBatchRootErrorLogsResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GajiBatchRootResponse {
    private String batchId;
    private String periode;
    private EProsesGaji status;
    private Integer totalPegawai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglProses;
    private String diProsesOleh;
    private String jabatanPemroses;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglVerifikasiTahap1;
    private String diVerifikasiOlehTahap1;
    private String jabatanVerifikasiTahap1;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglVerifikasiTahap2;
    private String diVerifikasiOlehTahap2;
    private String jabatanVerifikasiTahap2;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglPersetujuan;
    private String diSetujuiOleh;
    private String jabatanPenyetuju;
    private List<GajiBatchRootErrorLogsResponse> errorLogs;
    private String notes;
    private String mimeType;
    private String fileName;

    public static GajiBatchRootResponse from(GajiBatchRoot entity) {
        GajiBatchRootResponse response = new GajiBatchRootResponse();
        response.setBatchId(entity.getBatchId());
        response.setPeriode(entity.getPeriode());
        response.setStatus(entity.getStatus());
        response.setTotalPegawai(entity.getTotalPegawai());
        response.setTglProses(entity.getTglProses());
        response.setDiProsesOleh(entity.getDiProsesOleh());
        response.setJabatanPemroses(entity.getJabatanPemroses());
        response.setTglVerifikasiTahap1(entity.getTglVerifikasiTahap1());
        response.setDiVerifikasiOlehTahap1(entity.getDiVerifikasiOlehTahap1());
        response.setJabatanVerifikasiTahap1(entity.getJabatanVerifikasiTahap1());
        response.setTglVerifikasiTahap2(entity.getTglVerifikasiTahap2());
        response.setDiVerifikasiOlehTahap2(entity.getDiVerifikasiOlehTahap2());
        response.setJabatanVerifikasiTahap2(entity.getJabatanVerifikasiTahap2());
        response.setTglPersetujuan(entity.getTglPersetujuan());
        response.setDiSetujuiOleh(entity.getDiSetujuiOleh());
        response.setJabatanPenyetuju(entity.getJabatanPenyetuju());
        if (!entity.getErrorLogs().isEmpty())
            response.setErrorLogs(GajiBatchRootErrorLogsResponse.from(entity.getErrorLogs()));
        response.setNotes(entity.getNotes());
        response.setMimeType(entity.getMimeType());
        response.setFileName(entity.getFileName());
        return response;
    }
}
