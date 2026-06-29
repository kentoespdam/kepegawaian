package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LampiranSkQuery {
    private Long id;
    private EJenisSk ref;
    private Long refId;
    private String fileName;
    private String mimeType;
    private String notes;
    private Boolean disetujui;
    private String disetujuiOleh;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
}
