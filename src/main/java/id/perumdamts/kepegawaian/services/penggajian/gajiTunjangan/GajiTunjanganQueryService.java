package id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiTunjanganQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiTunjanganQueryService {
    private final GajiTunjanganQueryRepository queryRepository;

    public Page<GajiTunjanganResponse> findPage(EJenisTunjangan jenis, GajiTunjanganIndexQuery query) {
        return queryRepository.pageQuery(jenis, query);
    }

    public Optional<GajiTunjanganResponse> findById(EJenisTunjangan jenis, Long id) {
        return queryRepository.getByIdAndJenis(id, jenis);
    }
}
