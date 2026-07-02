package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenMiniProjection;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiKomponenQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKomponenQueryService {
    private final GajiKomponenQueryRepository queryRepository;

    public List<GajiKomponenMiniProjection> findAllKode(Long profilId) {
        return queryRepository.findAllKode(profilId);
    }

    public Page<GajiKomponenResponse> findPage(Long profilId, GajiKomponenIndexQuery query) {
        return queryRepository.pageQuery(profilId, query);
    }

    public Integer findLastUrut(Long profilId) {
        return queryRepository.findLastUrut(profilId);
    }

    public Optional<GajiKomponenResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
