package id.perumdamts.kepegawaian.dto.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CustomErrorResponse {
    private String path;
    private String error;
    private Integer status;
    private Long timestamp;
}
