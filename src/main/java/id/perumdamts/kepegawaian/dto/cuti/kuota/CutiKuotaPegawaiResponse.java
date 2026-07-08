package id.perumdamts.kepegawaian.dto.cuti.kuota;

import org.springframework.data.domain.Page;

import java.util.List;

public record CutiKuotaPegawaiResponse(
        Page<CutiKuotaResponse> page,
        List<CutiKuotaResponse> additional
) {
}
