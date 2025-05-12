package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RiwayatSpResponse {
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

    public static RiwayatSpResponse from(RiwayatSp entity) {
        RiwayatSpResponse response = new RiwayatSpResponse();
        response.setId(entity.getId());
        response.setPegawaiId(entity.getPegawai().getId());
        response.setNipam(entity.getNipam());
        response.setNama(entity.getNama());
        response.setOrganisasi(OrganisasiMiniResponse.from(entity.getOrganisasi()));
        response.setNamaOrganisasi(entity.getNamaOrganisasi());
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        response.setNamaJabatan(entity.getNamaJabatan());
        response.setNomorSp(entity.getNomorSp());
        response.setTanggalSp(entity.getTanggalSp());
        response.setJenisSp(JenisSpMiniResponse.from(entity.getJenisSp()));
        response.setSanksi(SanksiMiniResponse.from(entity.getSanksi()));
        response.setSanksiNotes(entity.getSanksiNotes());
        response.setTanggalEksekusiSanksi(entity.getTanggalEksekusiSanksi());
        response.setTanggalMulai(entity.getTanggalMulai());
        response.setTanggalSelesai(entity.getTanggalSelesai());
        response.setPenandaTangan(entity.getPenandaTangan());
        response.setJabatanPenandaTangan(entity.getJabatanPenandaTangan());
        response.setFileName(entity.getFileName());
        response.setMimeType(entity.getMimeType());
        response.setNotes(entity.getNotes());
        return response;
    }
}
