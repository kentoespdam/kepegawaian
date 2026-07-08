package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;

import java.util.Objects;

public record CutiJenisResponse(
        Long id,
        CutiJenisMiniResponse parent,
        String nama,
        Integer maxHari,
        Boolean potongKuotaTahunan
) {
    public static CutiJenisResponse from(CutiJenis cutiJenis) {
        return new CutiJenisResponse(
                cutiJenis.getId(),
                Objects.nonNull(cutiJenis.getParent()) ? CutiJenisMiniResponse.from(cutiJenis.getParent()) : null,
                cutiJenis.getNama(),
                cutiJenis.getMaxHari(),
                cutiJenis.getPotongKuotaTahunan()
        );
    }
}
