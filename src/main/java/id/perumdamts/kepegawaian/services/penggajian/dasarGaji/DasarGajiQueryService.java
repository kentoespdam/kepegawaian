package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.DasarGajiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DasarGajiQueryService {
    private final DasarGajiQueryRepository queryRepository;

    public Page<DasarGajiResponse> pageQuery(DasarGajiIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<DasarGajiResponse> listQuery() {
        return queryRepository.listQuery();
    }

    public DasarGajiResponse getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
    }
}
