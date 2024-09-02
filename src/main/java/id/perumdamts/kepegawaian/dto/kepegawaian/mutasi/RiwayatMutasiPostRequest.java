package id.perumdamts.kepegawaian.dto.kepegawaian.mutasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglBerakhir;
    @NotNull(message = "Jenis Mutasi is required")
    @Enumerated(EnumType.ORDINAL)
    private EJenisMutasi jenisMutasi;
    @NotNull(message = "Organisasi ID is required")
    @Min(value = 1, message = "Organisasi ID is required")
    private Long organisasiId;
    @NotNull(message = "Jabatan ID is required")
    @Min(value = 1, message = "Jabatan ID is required")
    private Long jabatanId;
    private Long organisasiLamaId;
    private Long jabatanLamaId;
    private String notes;

    @JsonIgnore
    public Specification<RiwayatMutasi> getSpecificationMutasi() {
        Specification<RiwayatMutasi> pegawaiSpec = Objects.isNull(getPegawaiId()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), getPegawaiId());
        Specification<RiwayatMutasi> organisasiSpec = Objects.isNull(getOrganisasiId()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasi").get("id"), getOrganisasiId());
        Specification<RiwayatMutasi> jabatanSpec = Objects.isNull(getJabatanId()) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), getJabatanId());
        return Specification.where(pegawaiSpec).and(organisasiSpec).and(jabatanSpec);
    }

    public static RiwayatMutasi toEntity(RiwayatSk riwayatSk, RiwayatMutasiPostRequest request, Organisasi organisasi, Jabatan jabatan, Organisasi organisasiLama, Jabatan jabatanLama) {
        RiwayatMutasi entity = new RiwayatMutasi();
        entity.setRiwayatSk(riwayatSk);
        entity.setPegawai(riwayatSk.getPegawai());
        entity.setTmtBerlaku(request.getTmtBerlaku());
        entity.setTglBerakhir(request.getTglBerakhir());
        entity.setJenisMutasi(request.getJenisMutasi());
        entity.setOrganisasi(organisasi);
        entity.setNamaOrganisasi(organisasi.getNama());
        entity.setJabatan(jabatan);
        entity.setNamaJabatan(jabatan.getNama());
        if (Objects.nonNull(organisasiLama) && Objects.nonNull(jabatanLama)) {
            entity.setOrganisasiLama(organisasiLama);
            entity.setNamaOrganisasiLama(organisasiLama.getNama());
            entity.setJabatanLama(jabatanLama);
            entity.setNamaJabatanLama(jabatanLama.getNama());
        }
        entity.setNotes(request.getNotes());
        return entity;
    }
}
