package id.perumdamts.kepegawaian.services.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiProfilQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiProfilQueryService {
    private final GajiProfilQueryRepository queryRepository;

    public Page<GajiProfilResponse> findAll(GajiProfilIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiProfilResponse> list(GajiProfilIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiProfilResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
