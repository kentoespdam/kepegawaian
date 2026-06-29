package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatTerminasiQuery {
    private Long id;
    private AlasanBerhentiResponse alasanTerminasi;
    private PegawaiResponse pegawai;
    private String nipam;
    private String nama;
    private String nomorSk;
    private RiwayatSkQuery skTerminasi;
    private LampiranSkQuery lampiranSkTerminasi;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private GolonganResponse golongan;
    private String namaGolongan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerminasi;
    private Integer tahunTerminasi;
    private Integer masaKerja;
    private String notes;
}
