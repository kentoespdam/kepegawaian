package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSkRequest extends PagedRequest {
    private Long pegawaiId;
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk jenisSk;
    private Long golonganId;
}
