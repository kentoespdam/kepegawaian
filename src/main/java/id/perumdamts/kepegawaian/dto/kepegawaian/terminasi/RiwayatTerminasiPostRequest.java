package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatTerminasiPostRequest extends RiwayatSkPostRequest {
    @NotNull(message = "Alasan Berhenti is required")
    @Min(value = 1, message = "Alasan Berhenti is required")
    private Long alasanTerminasiId;
    @NotNull(message = "Nipam is required")
    @NotEmpty(message = "Nipam ID is required")
    private String nipam;
    @NotNull(message = "Nama is required")
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotNull(message = "Organisasi ID is required")
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required")
    @Min(value = 1, message = "Jabatan ID must be greater than or equal to 1")
    private Long jabatanId;
    private MultipartFile fileName;

    @JsonIgnore
    public Specification<RiwayatTerminasi> getTerminasiSpecification() {
        Specification<RiwayatTerminasi> pegawaiSpec = Objects.isNull(getPegawaiId()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), getPegawaiId());
        Specification<RiwayatTerminasi> skSpec = Objects.isNull(getNomorSk()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("skTerminasi").get("nomorSk"), getNomorSk());
        return Specification.where(skSpec).and(pegawaiSpec);
    }

    public static RiwayatTerminasi toEntity(RiwayatTerminasiPostRequest request, AlasanBerhenti alasanTerminasi, RiwayatSk riwayatSk, Golongan golongan, Jabatan jabatan, Organisasi organisasi) {
        RiwayatTerminasi entity = new RiwayatTerminasi();
        entity.setAlasanTerminasi(alasanTerminasi);
        entity.setNipam(request.getNipam());
        entity.setNama(request.getNama());
        entity.setNomorSk(request.getNomorSk());
        entity.setSkTerminasi(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        if (Objects.nonNull(golongan)) {
            entity.setGolongan(golongan);
            entity.setNamaGolongan(golongan.getPangkat() + " - " + golongan.getGolongan());
        }
        entity.setTanggalTerminasi(request.getTmtBerlaku());
        entity.setTahunTerminasi(request.getTmtBerlaku().getYear());
        LocalDate tmtKerja = riwayatSk.getPegawai().getTmtKerja();
        Integer masaKerja = request.getTmtBerlaku().getYear() - tmtKerja.getYear();
        entity.setMasaKerja(masaKerja);
        entity.setNotes(request.getNotes());

        return entity;
    }
}
