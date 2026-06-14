package id.perumdamts.kepegawaian.dto.master.hariLibur;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HariLiburQuery {
    private Long id;
    private LocalDate tanggal;
    private String jenisLibur;
    private String notes;
}
