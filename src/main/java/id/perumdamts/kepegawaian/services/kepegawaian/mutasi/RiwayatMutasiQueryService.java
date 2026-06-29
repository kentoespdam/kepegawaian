package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatMutasiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiwayatMutasiQueryService {
    private final RiwayatMutasiQueryRepository queryRepository;

    public Page<RiwayatMutasiQuery> findPage(RiwayatMutasiRequest request) {
        return queryRepository.pageQuery(request);
    }

    public RiwayatMutasiQuery findById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Mutasi not found"));
    }
}
