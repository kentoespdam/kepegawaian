package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class DasarGajiPostRequest {
    @NotEmpty(message = "Deskripsi is required")
    private String deskripsi;
    @NotNull(message = "Tanggal Awal is required")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAwal;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhir;
    @NotNull(message = "Aktif is required")
    private Boolean aktif;

    @JsonIgnore
    public Specification<DasarGaji> getSpecification() {
        Specification<DasarGaji> deskripsiSpec = Objects.isNull(deskripsi) ? null :
                (root, query, cb) -> cb.equal(root.get("deskripsi"), deskripsi);

        Specification<DasarGaji> tglAwalSpec = Objects.isNull(tanggalAwal) ? null :
                (root, query, cb) -> cb.equal(root.get("tglAwal"), tanggalAwal);

        return Specification.where(deskripsiSpec).and(tglAwalSpec);
    }

    public static DasarGaji toEntity(DasarGajiPostRequest request) {
        DasarGaji dasarGaji = new DasarGaji();
        dasarGaji.setDeskripsi(request.getDeskripsi());
        dasarGaji.setTglAwal(request.getTanggalAwal());
        dasarGaji.setTglAkhir(request.getTanggalAkhir());
        dasarGaji.setAktif(request.getAktif());
        return dasarGaji;
    }
}
