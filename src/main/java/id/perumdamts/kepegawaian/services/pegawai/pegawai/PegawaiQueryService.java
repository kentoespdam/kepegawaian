package id.perumdamts.kepegawaian.services.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.*;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jooq.PegawaiQueryRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jooq.PegawaiRingkasanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PegawaiQueryService {
    private final PegawaiQueryRepository repository;
    private final PegawaiRingkasanQueryRepository ringkasanRepository;

    public Page<PegawaiResponse> findPage(PegawaiRequest request) {
        return repository.findPage(request);
    }

    public List<PegawaiListResponse> findAll(PegawaiRequest request) {
        return repository.findAll(request);
    }

    public PegawaiResponseDetail findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
    }

    public PegawaiResponse findByNipam(String nipam) {
        return repository.findByNipam(nipam)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
    }

    public PegawaiResponseRingkasan findRingkasan(Long id) {
        return ringkasanRepository.findRingkasan(id)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
    }

    public List<PegawaiListResponse> findByIds(List<Long> ids) {
        return repository.findByIds(ids);
    }
}
