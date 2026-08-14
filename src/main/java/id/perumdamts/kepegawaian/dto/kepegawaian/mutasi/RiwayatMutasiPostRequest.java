package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.GajiSk;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * DTO dedicated mutasi — TIDAK mewarisi {@code RiwayatSkPostRequest} agar kontrak
 * eksplisit. Berbeda dari terminasi, field SK-gaji (grup GajiSk) DI-PERTAHANKAN:
 * dikirim kondisional per {@code jenisMutasi} — MUTASI_GOLONGAN/GAJI/GAJI_BERKALA
 * mengirim golonganId + mkg* + kenaikanBerikutnya (+ gajiPokok untuk GAJI*),
 * lihat BE-REQUIREMENT-form-mutasi §5.
 *
 * Yang sengaja TIDAK ada:
 * - {@code updateMaster}: tidak dikirim FE; writeback pegawai ditangani eksplisit
 *   via {@code PegawaiWriteback} di command service (ADR-0023).
 * - {@code nipam}/{@code nama}: tidak dibaca mapper — snapshot diambil dari
 *   {@code RiwayatSk}/{@code Pegawai} (riwayatSk.getNipam()).
 */
@Data
public class RiwayatMutasiPostRequest {
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai ID is required")
    private Long pegawaiId;
    @NotEmpty(message = "Nomor SK is required")
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Jenis SK is required")
    private EJenisSk jenisSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal SK is required")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "TMT Berlaku is required")
    private LocalDate tmtBerlaku;

    // SK-gaji — kondisional per jenisMutasi; grup GajiSk tidak pernah diaktifkan controller
    @NotNull(message = "Golongan ID is required", groups = MutasiGolongan.class)
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1", groups = MutasiGolongan.class)
    private Long golonganId;
    @NotNull(message = "Gaji Pokok is required", groups = GajiSk.class)
    private Double gajiPokok;
    @Min(value = 0, message = "MKG Tahun is required", groups = GajiSk.class)
    private Integer mkgTahun;
    @Min(value = 0, message = "MKG Bulan is required", groups = GajiSk.class)
    private Integer mkgBulan;
    @NotNull(message = "Kenaikan Berikutnya is required", groups = GajiSk.class)
    private LocalDate kenaikanBerikutnya;
    private Integer mkgbTahun;
    private Integer mkgbBulan;

    private String notes;

    @NotNull(message = "Jenis Mutasi is required")
    private EJenisMutasi jenisMutasi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalBerakhir;
    @NotNull(message = "Organisasi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Organisasi ID is required", groups = MutasiJabatan.class)
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Jabatan ID is required", groups = MutasiJabatan.class)
    private Long jabatanId;
    @NotNull(message = "Profesi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Profesi ID is required", groups = MutasiJabatan.class)
    private Long profesiId;
    private Long organisasiLamaId;
    private Long jabatanLamaId;
    private Long golonganLamaId;
    private Long profesiLamaId;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecificationMutasi() {
        return SpecificationBuilder.<RiwayatMutasi>of()
                .addEqual(getNomorSk(), "riwayatSk", "nomorSk")
                .addEqual(getPegawaiId(), "pegawai", "id")
                .addEqual(getTanggalSk(), "riwayatSk", "tanggalSk")
                .build();
    }

}
