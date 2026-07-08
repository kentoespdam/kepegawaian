package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiReadMapper;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;

import java.time.LocalDate;
import java.util.List;

public record RiwayatTerminasiResponse(
        Long id,
        AlasanBerhentiResponse alasanTerminasi,
        PegawaiResponse pegawai,
        String nipam,
        String nama,
        String nomorSk,
        RiwayatSkResponse skTerminasi,
        LampiranSkResponse lampiranSkTerminasi,
        OrganisasiMiniResponse organisasi,
        String namaOrganisasi,
        JabatanMiniResponse jabatan,
        String namaJabatan,
        GolonganResponse golongan,
        String namaGolongan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalTerminasi,
        Integer tahunTerminasi,
        Integer masaKerja,
        String notes
) {
    public static RiwayatTerminasiResponse from(RiwayatTerminasi entity) {
        return new RiwayatTerminasiResponse(
                entity.getId(),
                AlasanBerhentiResponse.from(entity.getAlasanTerminasi()),
                PegawaiReadMapper.toResponse(entity.getPegawai()),
                entity.getNipam(),
                entity.getNama(),
                entity.getNomorSk(),
                RiwayatSkResponse.from(entity.getSkTerminasi()),
                null,
                OrganisasiMiniResponse.from(entity.getOrganisasi()),
                entity.getNamaOrganisasi(),
                JabatanMiniResponse.from(entity.getJabatan()),
                entity.getNamaJabatan(),
                entity.getGolongan() != null ? GolonganResponse.from(entity.getGolongan()) : null,
                entity.getNamaGolongan(),
                entity.getTanggalTerminasi(),
                entity.getTahunTerminasi(),
                entity.getMasaKerja(),
                entity.getNotes()
        );
    }

    public static RiwayatTerminasiResponse from(RiwayatTerminasi entity, List<LampiranSkResponse> lampiranSkResponses) {
        LampiranSkResponse lampiran = (lampiranSkResponses != null && !lampiranSkResponses.isEmpty())
                ? lampiranSkResponses.getFirst()
                : null;
        RiwayatTerminasiResponse base = RiwayatTerminasiResponse.from(entity);
        return new RiwayatTerminasiResponse(
                base.id(),
                base.alasanTerminasi(),
                base.pegawai(),
                base.nipam(),
                base.nama(),
                base.nomorSk(),
                base.skTerminasi(),
                lampiran,
                base.organisasi(),
                base.namaOrganisasi(),
                base.jabatan(),
                base.namaJabatan(),
                base.golongan(),
                base.namaGolongan(),
                base.tanggalTerminasi(),
                base.tahunTerminasi(),
                base.masaKerja(),
                base.notes()
        );
    }
}
