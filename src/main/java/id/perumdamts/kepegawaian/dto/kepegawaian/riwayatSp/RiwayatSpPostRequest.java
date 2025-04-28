package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class RiwayatSpPostRequest {
    @NotNull(message = "Nomor SP is required")
    @NotEmpty(message = "Nomor SP is required")
    private String nomorSp;
    @NotNull(message = "Pegawai ID is required")
    @Min(value = 1, message = "Pegawai ID required")
    private Long pegawaiId;
    @NotNull(message = "Organisasi ID is required")
    @Min(value = 1, message = "Organisasi ID required")
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required")
    @Min(value = 1, message = "Jabatan ID required")
    private Long jabatanId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal SP is required")
    private LocalDate tanggalSp;
    @NotNull(message = "Jenis SP is required")
    @Min(value = 1, message = "Jenis SP required")
    private Long jenisSpId;
    @NotNull(message = "Sanksi is required")
    @Min(value = 1, message = "Sanksi required")
    private Long sanksiId;
    private String sanksiNotes;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalEksekusiSanksi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Mulai is required")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal Selesai is required")
    private LocalDate tanggalSelesai;
    @NotNull(message = "Penanda Tangan is required")
    @NotEmpty(message = "Penanda Tangan is required")
    private String penandaTangan;
    @NotNull(message = "Jabatan Penanda Tangan is required")
    @NotEmpty(message = "Jabatan Penanda Tangan is required")
    private String jabatanPenandaTangan;
    private MultipartFile fileName;
    private String notes;

    public static RiwayatSp toEntity(
            RiwayatSpPostRequest request,
            JenisSp jenisSp,
            Pegawai pegawai,
            Jabatan jabatan,
            Organisasi organisasi
    ) {
        Sanksi sanksi = jenisSp.getSanksiSp().stream().filter(s -> s.getId().equals(request.getSanksiId()))
                .findFirst().orElse(null);
        RiwayatSp entity = new RiwayatSp();
        entity.setNomorSp(request.getNomorSp());
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        entity.setTanggalSp(request.getTanggalSp());
        entity.setJenisSp(jenisSp);
        entity.setSanksi(sanksi);
        entity.setSanksiNotes(request.getSanksiNotes());
        entity.setTanggalEksekusiSanksi(request.getTanggalEksekusiSanksi());
        entity.setTanggalMulai(request.getTanggalMulai());
        entity.setTanggalSelesai(request.getTanggalSelesai());
        entity.setPenandaTangan(request.getPenandaTangan());
        entity.setJabatanPenandaTangan(request.getJabatanPenandaTangan());
        entity.setNotes(request.getNotes());

        return entity;
    }

    @JsonIgnore
    public Specification<RiwayatSp> getSpecification() {
        Specification<RiwayatSp> nomorSpSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nomorSp"), nomorSp);
        Specification<RiwayatSp> pegawaiIdSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        return Specification.where(nomorSpSpec).and(pegawaiIdSpec);
    }
}
