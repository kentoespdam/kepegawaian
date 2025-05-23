package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatMutasiPostRequest extends RiwayatSkPostRequest {
    private String nipam;
    private String nama;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalBerakhir;
    @NotNull(message = "Jenis Mutasi is required")
    private EJenisMutasi jenisMutasi;
    @NotNull(message = "Organisasi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Organisasi ID is required", groups = MutasiJabatan.class)
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Jabatan ID is required", groups = MutasiJabatan.class)
    private Long jabatanId;
    @NotNull(message = "Profesi ID is required", groups = MutasiJabatan.class)
    @Min(value = 1, message = "Profesi ID is required", groups = MutasiJabatan.class)
    private Long profesiId;
    @NotNull(message = "Golongan ID is required", groups = MutasiGolongan.class)
    @Min(value = 1, message = "Golongan ID must be greater than or equal to 1", groups = MutasiGolongan.class)
    private Long golonganId;
    private Long organisasiLamaId;
    private Long jabatanLamaId;
    private Long golonganLamaId;
    private Long profesiLamaId;
    private String notes;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecificationMutasi() {
        Specification<RiwayatMutasi> skSpec = Objects.isNull(getNomorSk()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("riwayatSk").get("nomorSk"), getNomorSk());
        Specification<RiwayatMutasi> pegawaiSpec = Objects.isNull(getPegawaiId()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), getPegawaiId());
        return Specification.where(skSpec).and(pegawaiSpec);
    }

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk) {
        RiwayatMutasi entity = new RiwayatMutasi();
        entity.setNipam(riwayatSk.getNipam());
        entity.setNama(riwayatSk.getNama());
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTanggalBerakhir(request.getTanggalBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk, Golongan golongan, Golongan golonganLama) {
        RiwayatMutasi entity = toEntity(request, riwayatSk);
        entity.setGolongan(golongan);
        entity.setNamaGolongan(golongan.getPangkat() + " - " + golongan.getGolongan());
        entity.setGolonganLama(golonganLama);
        entity.setNamaGolonganLama(golonganLama.getPangkat() + " - " + golonganLama.getGolongan());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatMutasiPostRequest request, RiwayatSk riwayatSk, Organisasi organisasi, Jabatan jabatan, Profesi profesi, Organisasi organisasiLama, Jabatan jabatanLama, Profesi profesiLama) {
        RiwayatMutasi entity = toEntity(request, riwayatSk);
        if (Objects.nonNull(riwayatSk.getGolongan())) {
            entity.setGolongan(riwayatSk.getGolongan());
            entity.setNamaGolongan(riwayatSk.getGolongan().getPangkat() + " - " + riwayatSk.getGolongan().getGolongan());
            entity.setGolonganLama(riwayatSk.getGolongan());
            entity.setNamaGolonganLama(riwayatSk.getGolongan().getPangkat() + " - " + riwayatSk.getGolongan().getGolongan());
        }

        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        entity.setProfesi(profesi);
        entity.setNamaProfesi(profesi.getNama());

        entity.setOrganisasiLama(organisasiLama);
        entity.setNamaOrganisasiLama(organisasiLama.getNama());
        entity.setJabatanLama(jabatanLama);
        entity.setNamaJabatanLama(jabatanLama.getNama());
        entity.setProfesiLama(profesiLama);
        entity.setNamaProfesiLama(profesiLama.getNama());
        return entity;
    }

    public static RiwayatMutasi toEntity(RiwayatTerminasi riwayatTerminasi) {
        RiwayatMutasi entity = new RiwayatMutasi();
        Pegawai pegawai = riwayatTerminasi.getPegawai();
        entity.setPegawai(pegawai);
        entity.setNipam(pegawai.getNipam());
        entity.setNama(pegawai.getBiodata().getNama());
        entity.setTmtBerlaku(riwayatTerminasi.getTanggalTerminasi());
        entity.setTanggalBerakhir(riwayatTerminasi.getTanggalTerminasi());
        entity.setJenisMutasi(EJenisMutasi.TERMINASI);
        entity.setNotes(riwayatTerminasi.getNotes());

        entity.setOrganisasiLama(pegawai.getOrganisasi());
        entity.setNamaOrganisasiLama(pegawai.getOrganisasi().getNama());
        entity.setJabatanLama(pegawai.getJabatan());
        entity.setNamaJabatanLama(pegawai.getJabatan().getNama());
        entity.setProfesiLama(pegawai.getProfesi());
        entity.setNamaProfesiLama(pegawai.getProfesi().getNama());
        return entity;
    }
}
