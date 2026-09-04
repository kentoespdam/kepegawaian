package id.perumdamts.kepegawaian.services.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiKpiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKpiQueryService {
    private final GajiKpiQueryRepository queryRepository;

    public Page<GajiKpiResponse> findPage(GajiKpiIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiKpiResponse> findAll(GajiKpiListRequest query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiKpiResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
