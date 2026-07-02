package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchMasterProsesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiBatchMasterProsesQueryService {
    private final GajiBatchMasterProsesQueryRepository queryRepository;

    public Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiBatchMasterProsesResponse> findAll(GajiBatchMasterProsesIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiBatchMasterProsesResponse> findById(Long id) {
        return queryRepository.getById(id);
    }

    public List<GajiBatchMasterProsesResponse> findByMasterId(Long masterId) {
        return queryRepository.findByMasterId(masterId);
    }
}
