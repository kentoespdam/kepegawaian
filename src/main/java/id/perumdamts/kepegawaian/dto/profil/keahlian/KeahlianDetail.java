package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeahlianDetail extends KeahlianQuery {
    private List<LampiranRow> lampiran;
}
