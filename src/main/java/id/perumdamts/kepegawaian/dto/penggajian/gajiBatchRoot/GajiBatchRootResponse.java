package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLog.GajiBatchRootErrorLogsResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootLampiran.GajiBatchRootLampiranMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record GajiBatchRootResponse(
        String id,
        String periode,
        EProsesGaji status,
        Integer totalPegawai,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime tanggalProses,
        String diProsesOleh,
        String jabatanPemroses,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime tanggalVerifikasiTahap1,
        String diVerifikasiOlehTahap1,
        String jabatanVerifikasiTahap1,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime tanggalVerifikasiTahap2,
        String diVerifikasiOlehTahap2,
        String jabatanVerifikasiTahap2,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime tanggalPersetujuan,
        String diSetujuiOleh,
        String jabatanPenyetuju,
        String notes,
        List<GajiBatchRootErrorLogsResponse> errorLogs,
        List<GajiBatchRootLampiranMiniResponse> lampiran
) {
    public static GajiBatchRootResponse from(GajiBatchRoot entity) {
        List<GajiBatchRootErrorLogsResponse> errorLogs = entity.getErrorLogs() != null
                ? entity.getErrorLogs().stream().map(GajiBatchRootErrorLogsResponse::from).toList()
                : null;
        List<GajiBatchRootLampiranMiniResponse> lampiran = entity.getLampiranList() != null
                ? entity.getLampiranList().stream().map(GajiBatchRootLampiranMiniResponse::from).toList()
                : null;
        return new GajiBatchRootResponse(
                entity.getId(),
                entity.getPeriode(),
                entity.getStatus(),
                entity.getTotalPegawai(),
                entity.getTanggalProses(),
                entity.getDiProsesOleh(),
                entity.getJabatanPemroses(),
                entity.getTanggalVerifikasiTahap1(),
                entity.getDiVerifikasiOlehTahap1(),
                entity.getJabatanVerifikasiTahap1(),
                entity.getTanggalVerifikasiTahap2(),
                entity.getDiVerifikasiOlehTahap2(),
                entity.getJabatanVerifikasiTahap2(),
                entity.getTanggalPersetujuan(),
                entity.getDiSetujuiOleh(),
                entity.getJabatanPenyetuju(),
                entity.getNotes(),
                errorLogs,
                lampiran
        );
    }
}
