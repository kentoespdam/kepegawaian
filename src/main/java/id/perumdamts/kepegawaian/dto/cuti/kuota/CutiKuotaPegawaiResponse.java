package id.perumdamts.kepegawaian.dto.cuti.kuota;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class CutiKuotaPegawaiResponse {
    private Page<CutiKuotaResponse> page;
    private List<CutiKuotaResponse> additional;
}
