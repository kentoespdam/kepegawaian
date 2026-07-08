package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;

import java.time.LocalDateTime;

public record LampiranSkResponse(
        Long id,
        EJenisSk ref,
        Long refId,
        String fileName,
        String mimeType,
        String notes,
        boolean disetujui,
        String disetujuiOleh,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime tanggalDisetujui
) {
    public static LampiranSkResponse from(LampiranSk entity) {
        return new LampiranSkResponse(
                entity.getId(),
                entity.getRef(),
                entity.getRefId(),
                entity.getFileName(),
                entity.getMimeType(),
                entity.getNotes(),
                entity.getDisetujui(),
                entity.getDisetujuiOleh(),
                entity.getTanggalDisetujui()
        );
    }
}
