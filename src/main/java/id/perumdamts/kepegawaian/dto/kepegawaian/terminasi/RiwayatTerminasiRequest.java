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
        var builder = id.perumdamts.kepegawaian.utils.SpecificationBuilder.<id.perumdamts.kepegawaian.entities.pegawai.Pegawai>of()
                .addEqual(pegawaiId, "id")
                .addEqual(nipam, "nipam")
                .addLike(nama, "biodata", "nama")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(golonganId, "golongan", "id")
                .addEqual(organisasiId, "organisasi", "id")
                .addLessThanOrEqual(tanggalTerminasi, "tmtPensiun")
                .addEqual(id.perumdamts.kepegawaian.entities.commons.EStatusKerja.KARYAWAN_AKTIF, "statusKerja");
        // tahunPensiun: jendela 1 Januari–31 Desember tahun tersebut (portable, tanpa fungsi YEAR()
        // yang dialect-specific) — filter ini sebelumnya dideklarasikan tapi tidak pernah diterapkan.
        if (tahunPensiun != null) {
            builder.addBetween(
                    java.time.LocalDate.of(tahunPensiun, 1, 1),
                    java.time.LocalDate.of(tahunPensiun, 12, 31),
                    "tmtPensiun");
        }
        return builder.build();
    }
}
