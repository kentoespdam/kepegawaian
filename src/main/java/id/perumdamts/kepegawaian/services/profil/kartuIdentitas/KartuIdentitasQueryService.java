package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KartuIdentitasDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KartuIdentitasQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KartuIdentitasQueryService {
    private final KartuIdentitasQueryRepository queries;
    private final KartuIdentitasDetailQuery detail;

    public Page<KartuIdentitasQuery> pageQuery(KartuIdentitasIndexQuery query) {
        return queries.pageQuery(query);
    }

    public KartuIdentitasDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Kartu Identitas not found"));
    }
}
