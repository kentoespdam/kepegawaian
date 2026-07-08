package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;

import java.time.LocalDate;

public record PegawaiResponseDetail(
        Long id,
        String nipam,
        BiodataResponse biodata,
        EStatusPegawai statusPegawai,
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        ProfesiMiniResponse profesi,
        GolonganResponse golongan,
        GradeResponse grade,
        EStatusKerja statusKerja,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtKerja,
        RiwayatSkResponse skCapeg,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtPensiun,
        RiwayatSkResponse skPegawai,
        RiwayatSkResponse skGolongan,
        RiwayatSkResponse skJabatan,
        RiwayatSkResponse skMutasi,
        RiwayatSkResponse skKontrak,
        RiwayatSkResponse skGajiBerkala,
        Double gajiPokok,
        Double phdp,
        Integer jmlTanggungan,
        Integer mkgTahun,
        Integer mkgBulan,
        Long absensiId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtKontrakSelesai,
        Boolean isAskes,
        GajiPendapatanNonPajakResponse kodePajak,
        GajiProfilResponse gajiProfil,
        RumahDinasResponse rumahDinas,
        String email,
        String notes
) {}
