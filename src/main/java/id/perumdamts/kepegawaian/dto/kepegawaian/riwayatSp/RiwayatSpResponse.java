package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;

import java.time.LocalDate;

public record RiwayatSpResponse(
        Long id,
        Long pegawaiId,
        String nipam,
        String nama,
        OrganisasiMiniResponse organisasi,
        String namaOrganisasi,
        JabatanMiniResponse jabatan,
        String namaJabatan,
        String nomorSp,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSp,
        JenisSpMiniResponse jenisSp,
        SanksiMiniResponse sanksi,
        String sanksiNotes,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalEksekusiSanksi,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalMulai,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSelesai,
        String penandaTangan,
        String jabatanPenandaTangan,
        String fileName,
        String mimeType,
        String notes
) {
    public static RiwayatSpResponse from(RiwayatSp entity) {
        return new RiwayatSpResponse(
                entity.getId(),
                entity.getPegawai().getId(),
                entity.getNipam(),
                entity.getNama(),
                OrganisasiMiniResponse.from(entity.getOrganisasi()),
                entity.getNamaOrganisasi(),
                JabatanMiniResponse.from(entity.getJabatan()),
                entity.getNamaJabatan(),
                entity.getNomorSp(),
                entity.getTanggalSp(),
                JenisSpMiniResponse.from(entity.getJenisSp()),
                SanksiMiniResponse.from(entity.getSanksi()),
                entity.getSanksiNotes(),
                entity.getTanggalEksekusiSanksi(),
                entity.getTanggalMulai(),
                entity.getTanggalSelesai(),
                entity.getPenandaTangan(),
                entity.getJabatanPenandaTangan(),
                entity.getFileName(),
                entity.getMimeType(),
                entity.getNotes()
        );
    }
}
