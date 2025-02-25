package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterProsesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GajiBatchMasterProsesServiceImpl implements GajiBatchMasterProsesService {
    private final GajiBatchMasterProsesRepository repository;

    @Override
    public Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable()).map(GajiBatchMasterProsesResponse::from);
    }

    @Override
    public GajiBatchMasterProsesResponse findById(Long id) {
        return repository.findById(id).map(GajiBatchMasterProsesResponse::from).orElse(null);
    }

    @Override
    public List<GajiBatchMasterProsesResponse> findByMasterId(Long id) {
        return repository.findByGajiBatchMaster_Id(id).stream().map(GajiBatchMasterProsesResponse::from).toList();
    }
}
