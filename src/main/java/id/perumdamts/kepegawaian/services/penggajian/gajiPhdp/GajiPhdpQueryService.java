package id.perumdamts.kepegawaian.services.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiPhdpQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPhdpQueryService {
    private final GajiPhdpQueryRepository queryRepository;

    public Page<GajiPhdpResponse> findPage(GajiPhdpIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiPhdpResponse> findAll() {
        return queryRepository.listQuery();
    }

    public Optional<GajiPhdpResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
