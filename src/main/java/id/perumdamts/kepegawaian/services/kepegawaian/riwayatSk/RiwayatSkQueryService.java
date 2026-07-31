package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkListRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatSkQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiwayatSkQueryService {
    private final RiwayatSkQueryRepository queryRepository;

    public List<RiwayatSkQuery> findAll(RiwayatSkListRequest request) {
        return queryRepository.listQuery(request);
    }

    public Page<RiwayatSkQuery> findPage(RiwayatSkRequest request) {
        return queryRepository.pageQuery(request);
    }

    public RiwayatSkQuery findById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat SK not found"));
    }

    public List<RiwayatSkQuery> findByPegawai(Long pegawaiId) {
        return queryRepository.findByPegawai(pegawaiId);
    }

    public Page<RiwayatSkQuery> findByPegawaiId(Long pegawaiId, RiwayatSkRequest request) {
        return queryRepository.findByPegawaiId(pegawaiId, request);
    }
}
