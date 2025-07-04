package id.perumdamts.kepegawaian.dto.cuti.kuota;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CutiKuotaSisa {
    private Integer sisaCutiTahunIni;
    private Integer sisaCutiTahunLalu;
}
