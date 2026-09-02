package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchRootQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiBatchRootQueryService {
    private final GajiBatchRootQueryRepository queryRepository;

    public Page<GajiBatchRootResponse> findPage(GajiBatchRootIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public Optional<GajiBatchRootResponse> findById(String id) {
        return queryRepository.getById(id);
    }
}
