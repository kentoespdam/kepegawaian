package id.perumdamts.kepegawaian.services.cuti.jenis;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisListRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiJenisQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CutiJenisQueryService {
    private final CutiJenisQueryRepository queryRepository;

    public Page<CutiJenisResponse> findPage(CutiJenisRequest request) {
        return queryRepository.pageQuery(request);
    }

    public List<CutiJenisResponse> findList(CutiJenisListRequest request) {
        return queryRepository.listQuery(request);
    }

    public CutiJenisResponse findById(Long id) {
        return queryRepository.getById(id);
    }
}
