package id.perumdamts.kepegawaian.dto.cuti.jenis;

import lombok.Data;

@Data
public class CutiJenisResponse {
    private Long id;
    private CutiJenisMiniResponse parent;
    private String nama;
    private Integer maxHari;
    private Boolean potongKuotaTahunan;

    public static CutiJenisResponse from(id.perumdamts.kepegawaian.entities.cuti.CutiJenis cutiJenis) {
        return new CutiJenisResponse() {{
            setId(cutiJenis.getId());
            setParent(cutiJenis.getParent() != null ? CutiJenisMiniResponse.from(cutiJenis.getParent()) : null);
            setNama(cutiJenis.getNama());
            setMaxHari(cutiJenis.getMaxHari());
            setPotongKuotaTahunan(cutiJenis.getPotongKuotaTahunan());
        }};
    }
}
