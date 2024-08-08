package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.penggajian.LampiranSk;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LampiranSkResponse {
    private Long id;
    private EJenisSk ref;
    private Long refId;
    private String fileName;
    private String mimeType;
    private String notes;
    private boolean disetujui;
    private String disetujuiOleh;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;

    public static LampiranSkResponse from(LampiranSk entity) {
        LampiranSkResponse response = new LampiranSkResponse();
        response.setId(entity.getId());
        response.setRef(entity.getRef());
        response.setRefId(entity.getRefId());
        response.setFileName(entity.getFileName());
        response.setMimeType(entity.getMimeType());
        response.setNotes(entity.getNotes());
        response.setDisetujui(entity.getDisetujui());
        response.setDisetujuiOleh(entity.getDisetujuiOleh());
        response.setTanggalDisetujui(entity.getTanggalDisetujui());
        return response;
    }
}
