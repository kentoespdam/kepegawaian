package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KenaikanBerkalaRequest {
    @NotNull(message = "filter is required")
    private EFilterKenaikanBerkala filter = EFilterKenaikanBerkala.BULAN_INI;
    @NotNull(message = "jenis sk is required")
    private EJenisKenaikanBerkala jenisSk;

    @Override
    public String toString() {
        return "?filter=" + filter + "& jenis_sk=" + jenisSk;
    }
}
