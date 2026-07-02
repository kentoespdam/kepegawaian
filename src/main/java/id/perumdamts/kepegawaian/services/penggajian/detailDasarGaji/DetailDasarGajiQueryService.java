package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.DetailDasarGajiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetailDasarGajiQueryService {
    private final DetailDasarGajiQueryRepository queryRepository;

    public Page<DetailDasarGajiResponse> findPage(DetailDasarGajiIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<DetailDasarGajiResponse> findList(DetailDasarGajiIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<DetailDasarGajiResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
