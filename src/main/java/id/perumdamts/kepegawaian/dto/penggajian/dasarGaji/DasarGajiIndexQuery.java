package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DasarGajiIndexQuery extends PagedRequest {
    private String deskripsi;
    private LocalDate tanggalAwal;
    private LocalDate tanggalAkhir;
    private Boolean aktif;
}
