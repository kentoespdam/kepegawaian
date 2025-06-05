package id.perumdamts.kepegawaian.dto.master.hariLibur;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HariLiburResponse {
    private Long id;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal;
    @Enumerated(EnumType.STRING)
    private EJenisLibur jenisLibur;
    private String notes;

    public static HariLiburResponse from(HariLibur entity) {
        HariLiburResponse response = new HariLiburResponse();
        response.setId(entity.getId());
        response.setTanggal(entity.getTanggal());
        response.setJenisLibur(entity.getJenisLibur());
        response.setNotes(entity.getNotes());
        return response;
    }
}
