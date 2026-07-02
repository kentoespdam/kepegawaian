package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiTunjanganIndexQuery extends PagedRequest {
    private EJenisTunjangan jenis;
    private Long levelId;
    private Long golonganId;
}
