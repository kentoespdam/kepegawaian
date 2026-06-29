package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatSkQuery {
    private Long id;
    private String nipam;
    private String nama;
    private String nomorSk;
    private EJenisSk jenisSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    private GolonganResponse golongan;
    private Double gajiPokok;
    private Integer mkgTahun;
    private Integer mkgBulan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate kenaikanBerikutnya;
    private Integer mkgbTahun;
    private Integer mkgbBulan;
    private Boolean updateMaster;
    private String notes;
}
