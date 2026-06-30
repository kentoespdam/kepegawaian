package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPegawaiResponse;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaResponse;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaSisa;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiKuotaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiKuotaQueryService {
    private final CutiKuotaQueryRepository queryRepository;

    public CutiKuotaPegawaiResponse findPage(CutiKuotaRequest request) {
        return queryRepository.pageQuery(request);
    }

    public CutiKuotaResponse findById(Long id) {
        return queryRepository.getById(id);
    }

    public CutiKuotaSisa findByPegawai(Long pegawaiId, Integer tahun) {
        return queryRepository.findByPegawai(pegawaiId, tahun);
    }
}
