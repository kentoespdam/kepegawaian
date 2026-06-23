package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProfilKeluargaDetail extends ProfilKeluargaQuery {
    private List<LampiranRow> lampiran;
}