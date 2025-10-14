package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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
        return SpecificationBuilder.<RiwayatTerminasi>of()
                .addEqual(tahunPensiun, "tahunPensiun")
                .addEqual(alasanTerminasiId, "alasanTerminasi", "id")
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(nipam, "pegawai", "nipam")
                .addEqual(nama, "pegawai", "biodata", "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(golonganId, "golongan", "id")
                .addEqual(nomorSk, "nomorSk")
                .addEqual(tanggalTerminasi, "tanggalTerminasi")
                .build();
    }

    @JsonIgnore
    public Specification<Pegawai> getCalonPensiunSpecification() {
        return SpecificationBuilder.<Pegawai>of()
                .addEqual(pegawaiId, "id")
                .addEqual(nipam, "nipam")
                .addEqual(nama, "biodata", "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(golonganId, "golongan", "id")
                .addLessThan(tanggalTerminasi, "tmtPensiun")
                .addEqual(EStatusKerja.KARYAWAN_AKTIF, "statusKerja")
                .build();
    }
}
