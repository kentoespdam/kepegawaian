package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatMutasiQuery {
    private Long id;
    private String nipam;
    private String nama;
    private RiwayatSkQuery skMutasi;
    private EJenisMutasi jenisMutasi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalBerakhir;
    private GolonganResponse golongan;
    private OrganisasiMiniResponse organisasi;
    private String namaOrganisasi;
    private JabatanMiniResponse jabatan;
    private String namaJabatan;
    private ProfesiMiniResponse profesi;
    private String namaProfesi;
    private GolonganResponse golonganLama;
    private OrganisasiMiniResponse organisasiLama;
    private String namaOrganisasiLama;
    private JabatanMiniResponse jabatanLama;
    private String namaJabatanLama;
    private ProfesiMiniResponse profesiLama;
    private String namaProfesiLama;
    private String notes;
}
