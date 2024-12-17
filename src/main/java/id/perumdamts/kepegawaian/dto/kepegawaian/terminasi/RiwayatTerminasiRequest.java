package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatTerminasiRequest extends CommonPageRequest {
    private Integer tahunPensiun;
    private Long alasanTerminasiId;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Long jabatanId;
    private Long organisasiId;
    private Long golonganId;
    private String nomorSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerminasi;

    @JsonIgnore
    public Specification<RiwayatTerminasi> getSpecification() {
        Specification<RiwayatTerminasi> tahunPensiunSpec = Objects.isNull(tahunPensiun) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tahunPensiun"), tahunPensiun);
        Specification<RiwayatTerminasi> alasanBerhentiSpec = Objects.isNull(alasanTerminasiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("alasanTerminasi").get("id"), alasanTerminasiId);
        Specification<RiwayatTerminasi> pegawaiIdSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<RiwayatTerminasi> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("nipam"), nipam);
        Specification<RiwayatTerminasi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("pegawai").get("biodata").get("nama"), "%" + nama + "%");
        Specification<RiwayatTerminasi> jabatanIdSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<RiwayatTerminasi> golonganIdSpec = Objects.isNull(golonganId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("golongan").get("id"), golonganId);
        Specification<RiwayatTerminasi> nomorSkSpec = Objects.isNull(nomorSk) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nomorSk"), nomorSk);

        return Specification.where(tahunPensiunSpec).and(alasanBerhentiSpec).and(pegawaiIdSpec).and(nipamSpec).and(namaSpec).and(jabatanIdSpec).and(golonganIdSpec).and(nomorSkSpec);
    }

    @JsonIgnore
    public Specification<Pegawai> getCalonPensiunSpecification() {
        Specification<Pegawai> pegawaiIdSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), pegawaiId);
        Specification<Pegawai> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nipam"), nipam);
        Specification<Pegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("biodata").get("nama"), "%" + nama + "%");
        Specification<Pegawai> jabatanIdSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<Pegawai> golonganIdSpec = Objects.isNull(golonganId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("golongan").get("id"), golonganId);
        Specification<Pegawai> tmtPensiunSpec = Objects.isNull(tanggalTerminasi) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("tmtPensiun"), tanggalTerminasi);
        Specification<Pegawai> statusKerjaSpec = (root, query, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("statusKerja"), EStatusKerja.KARYAWAN_AKTIF);
        return Specification.where(pegawaiIdSpec).and(nipamSpec).and(namaSpec).and(jabatanIdSpec).and(golonganIdSpec)
                .and(tmtPensiunSpec).and(statusKerjaSpec);
    }
}
