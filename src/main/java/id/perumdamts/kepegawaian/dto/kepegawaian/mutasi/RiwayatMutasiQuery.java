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

import java.time.LocalDate;

public record RiwayatMutasiQuery(
        Long id,
        String nipam,
        String nama,
        RiwayatSkQuery skMutasi,
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
) {}
