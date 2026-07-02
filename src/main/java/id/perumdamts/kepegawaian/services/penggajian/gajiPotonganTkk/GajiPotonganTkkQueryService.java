package id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiPotonganTkkQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPotonganTkkQueryService {
    private final GajiPotonganTkkQueryRepository queryRepository;

    public Page<GajiPotonganTkkResponse> findPage(GajiPotonganTkkIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public Optional<GajiPotonganTkkResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
