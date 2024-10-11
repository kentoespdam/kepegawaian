package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSp;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.ORDINAL)
    private EJenisSp jenisSp;
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
        response.setJenisSp(entity.getJenisSp());
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
