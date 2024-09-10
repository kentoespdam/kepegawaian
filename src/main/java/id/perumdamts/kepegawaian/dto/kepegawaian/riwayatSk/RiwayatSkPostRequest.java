package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiwayatSkPostRequest {
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai ID must be greater than or equal to 1")
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
    @NotNull(message = "Golongan ID is required", groups = GajiSk.class)
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1", groups = GajiSk.class)
    private Long golonganId;
    @NotNull(message = "Gaji Pokok is required", groups = GajiSk.class)
    private Double gajiPokok;
    @Min(value = 0, message = "MKG Tahun must be greater than or equal to 1", groups = GajiSk.class)
    private Integer mkgTahun;
    @Min(value = 0, message = "MKG Bulan must be greater than or equal to 1", groups = GajiSk.class)
    private Integer mkgBulan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Kenaikan Berikutnya is required", groups = GajiSk.class)
    private LocalDate kenaikanBerikutnya;
    @JsonSerialize(using = LocalDateSerializer.class)
    private Integer mkgbTahun;
    @JsonSerialize(using = LocalDateSerializer.class)
    private Integer mkgbBulan;
    private Boolean updateMaster = false;
    private String notes;


    @JsonIgnore
    public Specification<RiwayatSk> getSpecification() {
        Specification<RiwayatSk> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, cb) -> cb.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatSk> nomorSkSpec = Objects.isNull(nomorSk) ? null :
                (root, query, cb) -> cb.equal(root.get("nomorSk"), nomorSk);
        Specification<RiwayatSk> jenisSkSpec = Objects.isNull(jenisSk) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisSk"), jenisSk);
        return Specification.where(pegawaiSpec).and(nomorSkSpec).and(jenisSkSpec);
    }

    public static RiwayatSk toEntity(RiwayatSkPostRequest request, Pegawai pegawai){
        RiwayatSk entity = new RiwayatSk();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setJenisSk(request.getJenisSk());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setGajiPokok(request.getGajiPokok());
        entity.setMkgTahun(request.getMkgTahun());
        entity.setMkgBulan(request.getMkgBulan());
        entity.setKenaikanBerikutnya(request.getKenaikanBerikutnya());
        entity.setMkgbTahun(request.getMkgbTahun());
        entity.setMkgbBulan(request.getMkgbBulan());
        entity.setUpdateMaster(request.getUpdateMaster());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatSk toEntity(RiwayatSkPostRequest request, Pegawai pegawai, Golongan golongan) {
        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai);
        entity.setGolongan(golongan);
        return entity;
    }
}
