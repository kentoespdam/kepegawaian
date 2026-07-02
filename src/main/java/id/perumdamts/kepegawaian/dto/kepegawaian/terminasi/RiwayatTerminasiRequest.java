package id.perumdamts.kepegawaian.dto.kepegawaian.terminasi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatTerminasiRequest extends PagedRequest {
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

    @com.fasterxml.jackson.annotation.JsonIgnore
    public org.springframework.data.jpa.domain.Specification<id.perumdamts.kepegawaian.entities.pegawai.Pegawai> getCalonPensiunSpecification() {
        return id.perumdamts.kepegawaian.utils.SpecificationBuilder.<id.perumdamts.kepegawaian.entities.pegawai.Pegawai>of()
                .addEqual(pegawaiId, "id")
                .addEqual(nipam, "nipam")
                .addEqual(nama, "biodata", "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(golonganId, "golongan", "id")
                .addLessThan(tanggalTerminasi, "tmtPensiun")
                .addEqual(id.perumdamts.kepegawaian.entities.commons.EStatusKerja.KARYAWAN_AKTIF, "statusKerja")
                .build();
    }
}
