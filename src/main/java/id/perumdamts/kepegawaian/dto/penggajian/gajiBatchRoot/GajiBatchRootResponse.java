package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLog.GajiBatchRootErrorLogsResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran.GajiBatchRootLampiranMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GajiBatchRootResponse {
    private String id;
    private String periode;
    private EProsesGaji status;
    private Integer totalPegawai;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalProses;
    private String diProsesOleh;
    private String jabatanPemroses;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalVerifikasiTahap1;
    private String diVerifikasiOlehTahap1;
    private String jabatanVerifikasiTahap1;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalVerifikasiTahap2;
    private String diVerifikasiOlehTahap2;
    private String jabatanVerifikasiTahap2;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPersetujuan;
    private String diSetujuiOleh;
    private String jabatanPenyetuju;
    private String notes;
    private List<GajiBatchRootErrorLogsResponse> errorLogs;
    private List<GajiBatchRootLampiranMiniResponse> lampiran;

    public static GajiBatchRootResponse from(GajiBatchRoot entity) {
        GajiBatchRootResponse response = new GajiBatchRootResponse();
        response.setId(entity.getId());
        response.setPeriode(entity.getPeriode());
        response.setStatus(EProsesGaji.values()[entity.getStatus()]);
        response.setTotalPegawai(entity.getTotalPegawai());
        response.setTanggalProses(entity.getTanggalProses());
        response.setDiProsesOleh(entity.getDiProsesOleh());
        response.setJabatanPemroses(entity.getJabatanPemroses());
        response.setTanggalVerifikasiTahap1(entity.getTanggalVerifikasiTahap1());
        response.setDiVerifikasiOlehTahap1(entity.getDiVerifikasiOlehTahap1());
        response.setJabatanVerifikasiTahap1(entity.getJabatanVerifikasiTahap1());
        response.setTanggalVerifikasiTahap2(entity.getTanggalVerifikasiTahap2());
        response.setDiVerifikasiOlehTahap2(entity.getDiVerifikasiOlehTahap2());
        response.setJabatanVerifikasiTahap2(entity.getJabatanVerifikasiTahap2());
        response.setTanggalPersetujuan(entity.getTanggalPersetujuan());
        response.setDiSetujuiOleh(entity.getDiSetujuiOleh());
        response.setJabatanPenyetuju(entity.getJabatanPenyetuju());
        response.setNotes(entity.getNotes());
        response.setErrorLogs(entity.getErrorLogs().stream().map(GajiBatchRootErrorLogsResponse::from).toList());
        response.setLampiran(entity.getLampiranList().stream().map(GajiBatchRootLampiranMiniResponse::from).toList());
        return response;
    }

//    public static GajiBatchRootResponse from(GajiBatchRoot entity, List<GajiBatchRootErrorLogs> errorLogs) {
//        GajiBatchRootResponse response = from(entity);
//        if (errorLogs != null)
//            response.setErrorLogs(errorLogs.stream().map(GajiBatchRootErrorLogsResponse::from).toList());
//        response.setNotes(entity.getNotes());
//        return response;
//    }
}