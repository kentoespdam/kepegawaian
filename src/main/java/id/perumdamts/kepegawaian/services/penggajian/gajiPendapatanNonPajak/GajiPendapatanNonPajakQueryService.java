package id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiPendapatanNonPajakQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPendapatanNonPajakQueryService {
    private final GajiPendapatanNonPajakQueryRepository queryRepository;

    public Page<GajiPendapatanNonPajakResponse> findPage(GajiPendapatanNonPajakIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiPendapatanNonPajakResponse> findAll(GajiPendapatanNonPajakListRequest query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiPendapatanNonPajakResponse> findById(Long id) {
        return queryRepository.getById(id);
    }
}
