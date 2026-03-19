package id.perumdamts.kepegawaian.dto.pegawai;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PegawaiBatchIdsRequest {
    @NotEmpty
    private List<Long> ids;
}
