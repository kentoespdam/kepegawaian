package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatSpQuery {
    private Long id;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private String nomorSp;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSp;
    private JenisSpMiniResponse jenisSp;
    private SanksiMiniResponse sanksi;
    private String sanksiNotes;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalEksekusiSanksi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String penandaTangan;
    private String jabatanPenandaTangan;
    private String fileName;
    private String mimeType;
    private String notes;
}
