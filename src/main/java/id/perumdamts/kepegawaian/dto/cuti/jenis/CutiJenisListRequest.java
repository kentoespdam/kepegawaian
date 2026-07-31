package id.perumdamts.kepegawaian.dto.cuti.jenis;

import lombok.Data;

@Data
public class CutiJenisListRequest {
    private Long parentId;
    private String nama;
}
