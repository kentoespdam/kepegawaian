package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.DasarGajiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DasarGajiQueryService {
    private final DasarGajiQueryRepository queryRepository;

    public Page<DasarGajiResponse> findPage(DasarGajiIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<DasarGajiResponse> findList(DasarGajiIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<DasarGajiResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
