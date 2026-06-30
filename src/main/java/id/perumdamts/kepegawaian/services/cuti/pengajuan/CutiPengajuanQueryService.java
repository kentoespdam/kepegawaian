package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiPengajuanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiPengajuanQueryService {
    private final CutiPengajuanQueryRepository queryRepository;

    public Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request) {
        return queryRepository.pageQuery(request);
    }

    public CutiPengajuanResponse findById(Long id) {
        return queryRepository.getById(id);
    }
}
