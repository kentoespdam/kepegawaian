package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;

import java.time.LocalDate;
import java.util.Objects;

public record RiwayatMutasiResponse(
        Long id,
        String nipam,
        String nama,
        RiwayatSkResponse skMutasi,
        EJenisMutasi jenisMutasi,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtBerlaku,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalBerakhir,
        GolonganResponse golongan,
        OrganisasiMiniResponse organisasi,
        String namaOrganisasi,
        JabatanMiniResponse jabatan,
        String namaJabatan,
        ProfesiMiniResponse profesi,
        String namaProfesi,
        GolonganResponse golonganLama,
        OrganisasiMiniResponse organisasiLama,
        String namaOrganisasiLama,
        JabatanMiniResponse jabatanLama,
        String namaJabatanLama,
        ProfesiMiniResponse profesiLama,
        String namaProfesiLama,
        String notes
) {
    public static RiwayatMutasiResponse from(RiwayatMutasi entity) {
        return new RiwayatMutasiResponse(
                entity.getId(),
                entity.getNipam(),
                entity.getNama(),
                Objects.nonNull(entity.getRiwayatSk()) ? RiwayatSkResponse.from(entity.getRiwayatSk()) : null,
                entity.getJenisMutasi(),
                entity.getTmtBerlaku(),
                entity.getTanggalBerakhir(),
                Objects.nonNull(entity.getGolongan()) ? GolonganResponse.from(entity.getGolongan()) : null,
                Objects.nonNull(entity.getOrganisasi()) ? OrganisasiMiniResponse.from(entity.getOrganisasi()) : null,
                entity.getNamaOrganisasi(),
                Objects.nonNull(entity.getJabatan()) ? JabatanMiniResponse.from(entity.getJabatan()) : null,
                entity.getNamaJabatan(),
                Objects.nonNull(entity.getProfesi()) ? ProfesiMiniResponse.from(entity.getProfesi()) : null,
                entity.getNamaProfesi(),
                Objects.nonNull(entity.getGolonganLama()) ? GolonganResponse.from(entity.getGolonganLama()) : null,
                Objects.nonNull(entity.getOrganisasiLama()) ? OrganisasiMiniResponse.from(entity.getOrganisasiLama()) : null,
                entity.getNamaOrganisasiLama(),
                Objects.nonNull(entity.getJabatanLama()) ? JabatanMiniResponse.from(entity.getJabatanLama()) : null,
                entity.getNamaJabatanLama(),
                Objects.nonNull(entity.getProfesiLama()) ? ProfesiMiniResponse.from(entity.getProfesiLama()) : null,
                entity.getNamaProfesiLama(),
                entity.getNotes()
        );
    }
}
