package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LampiranProfilResponse {
    private Long id;
    private EJenisLampiranProfil ref;
    private Long refId;
    private String fileName;
    private String mimeType;
    private String notes;
    private boolean disetujui;
    private String disetujuiOleh;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;

    public static LampiranProfilResponse from(LampiranProfil entity) {
        LampiranProfilResponse response = new LampiranProfilResponse();
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
