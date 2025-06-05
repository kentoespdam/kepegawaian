package id.perumdamts.kepegawaian.dto.master.hariLibur;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class HariLiburPostRequest {
    @NotNull(message = "Tanggal is required")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal;
    @NotNull(message = "Jenis Libur is required")
    @Enumerated(EnumType.ORDINAL)
    private EJenisLibur jenisLibur;
    private String notes;

    @JsonIgnore
    public Specification<HariLibur> getSpecification() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("tanggal"), this.tanggal),
                cb.equal(root.get("jenisLibur"), this.jenisLibur)
        );
    }

    public static HariLibur toEntity(HariLiburPostRequest request) {
        HariLibur entity = new HariLibur();
        entity.setTanggal(request.getTanggal());
        entity.setJenisLibur(request.getJenisLibur());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
