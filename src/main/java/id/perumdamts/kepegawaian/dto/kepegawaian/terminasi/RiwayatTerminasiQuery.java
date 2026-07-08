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

import java.time.LocalDate;

public record RiwayatTerminasiQuery(
        Long id,
        AlasanBerhentiResponse alasanTerminasi,
        PegawaiResponse pegawai,
        String nipam,
        String nama,
        String nomorSk,
        RiwayatSkQuery skTerminasi,
        LampiranSkQuery lampiranSkTerminasi,
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
) {}
