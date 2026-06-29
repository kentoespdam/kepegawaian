package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatTerminasiQueryRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jooq.PegawaiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiwayatTerminasiQueryService {
    private final RiwayatTerminasiQueryRepository queryRepository;
    private final PegawaiQueryRepository pegawaiQueryRepository;

    public Page<RiwayatTerminasiQuery> findPage(RiwayatTerminasiRequest request) {
        return queryRepository.pageQuery(request)
                .map(q -> {
                    pegawaiQueryRepository.findByNipam(q.getNipam()).ifPresent(q::setPegawai);
                    return q;
                });
    }

    public RiwayatTerminasiQuery findById(Long id) {
        RiwayatTerminasiQuery q = queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Terminasi not found"));
        pegawaiQueryRepository.findByNipam(q.getNipam()).ifPresent(q::setPegawai);
        return q;
    }
}
