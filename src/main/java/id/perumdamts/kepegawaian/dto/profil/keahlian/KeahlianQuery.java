package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeahlianQuery {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private Long jenisKeahlianId;
    private JenisKeahlianResponse jenisKeahlian;
    private String kualifikasi;
    private Boolean sertifikasi;
    private String institusi;
    private Integer tahun;
    private String masaBerlaku;
    private Boolean disetujui;
    private LocalDateTime tanggalPengajuan;
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;
    private Byte changedStatus;
}
