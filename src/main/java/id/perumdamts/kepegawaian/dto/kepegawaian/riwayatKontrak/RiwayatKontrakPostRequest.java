package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class RiwayatKontrakPostRequest {
//    @Enumerated(EnumType.ORDINAL)
    private EJenisKontrak jenisKontrak;
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai ID must be greater than or equal to 1")
    private Long pegawaiId;
    @NotEmpty(message = "NIPAM is required")
    private String nipam;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotEmpty(message = "Nomor Kontrak is required")
    private String nomorKontrak;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal SK is required")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Mulai is required")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Selesai is required", groups = PerpanjanganKontrak.class)
    private LocalDate tanggalSelesai;
    @NotNull(message = "Golongan ID is required", groups = KontrakToCapeg.class)
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1", groups = KontrakToCapeg.class)
    private Long golonganId;
    @Min(value = 0, message = "Gaji Pokok must be greater than or equal to 0")
    private Double gajiPokok;
    private Boolean isLatest = false;
    private String notes;
    private MultipartFile fileName;

    @JsonIgnore
    public Specification<RiwayatKontrak> getSpecification() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), this.pegawaiId),
                criteriaBuilder.equal(root.get("nipam"), this.nipam),
                criteriaBuilder.equal(root.get("nomorKontrak"), this.nomorKontrak),
                criteriaBuilder.equal(root.get("jenisKontrak"), this.jenisKontrak)
        );
    }

    public static RiwayatKontrak toEntity(RiwayatKontrakPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = new RiwayatKontrak();
        entity.setPegawai(pegawai);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorKontrak(request.getNomorKontrak());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setIsLatest(request.getIsLatest());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatKontrak toEntity(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = new RiwayatKontrak();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setNomorKontrak(request.getNomorSk());
        entity.setTanggalSk(request.getTanggalSk());
        entity.setTanggalMulai(request.getTmtBerlakuSk());
        entity.setTanggalSelesai(request.getTmtKontrakSelesai());
        entity.setIsLatest(true);
        entity.setNotes(request.getNotes());
        return entity;
    }

}
