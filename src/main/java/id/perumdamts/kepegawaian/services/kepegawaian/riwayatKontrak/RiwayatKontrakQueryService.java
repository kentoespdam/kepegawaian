package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatKontrakQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiwayatKontrakQueryService {
    private final RiwayatKontrakQueryRepository queryRepository;

    public Page<RiwayatKontrakQuery> findPage(RiwayatKontrakRequest request) {
        return queryRepository.pageQuery(request);
    }

    public RiwayatKontrakQuery findById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Kontrak not found"));
    }
}
