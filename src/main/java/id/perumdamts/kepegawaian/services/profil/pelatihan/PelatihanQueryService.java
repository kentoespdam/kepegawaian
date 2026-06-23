package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PelatihanDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PelatihanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PelatihanQueryService {
    private final PelatihanQueryRepository queries;
    private final PelatihanDetailQuery detail;

    public Page<PelatihanQuery> pageQuery(PelatihanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PelatihanDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pelatihan not found"));
    }
}