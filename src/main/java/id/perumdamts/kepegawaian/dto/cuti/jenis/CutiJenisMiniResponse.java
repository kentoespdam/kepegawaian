package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import lombok.Data;

@Data
public class CutiJenisMiniResponse {
    private Long id;
    private String nama;

    public static CutiJenisMiniResponse from(CutiJenis entity) {
        CutiJenisMiniResponse cutiJenisMiniResponse = new CutiJenisMiniResponse();
        cutiJenisMiniResponse.setId(entity.getId());
        cutiJenisMiniResponse.setNama(entity.getNama());
        return cutiJenisMiniResponse;
    }
}
