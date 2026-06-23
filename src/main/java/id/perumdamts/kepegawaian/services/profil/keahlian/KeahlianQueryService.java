package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KeahlianDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KeahlianQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeahlianQueryService {
    private final KeahlianQueryRepository queries;
    private final KeahlianDetailQuery detail;

    public Page<KeahlianQuery> pageQuery(KeahlianIndexQuery query) {
        return queries.pageQuery(query);
    }

    public KeahlianDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Keahlian not found"));
    }
}
