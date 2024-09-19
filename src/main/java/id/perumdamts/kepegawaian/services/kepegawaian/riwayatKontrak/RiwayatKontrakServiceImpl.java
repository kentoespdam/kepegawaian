package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatKontrakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RiwayatKontrakServiceImpl implements RiwayatKontrakService {
    private final RiwayatKontrakRepository repository;
    @Override
    public Page<RiwayatKontrakResponse> findByPegawaiId(Long id, RiwayatKontrakRequest request) {
        if (Objects.isNull(request.getSortBy()) || request.getSortBy().isEmpty()) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        request.setPegawaiId(id);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RiwayatKontrakResponse::from);
    }
}
