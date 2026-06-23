package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PendidikanDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PendidikanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendidikanQueryService {
    private final PendidikanQueryRepository queries;
    private final PendidikanDetailQuery detail;

    public Page<PendidikanQuery> pageQuery(PendidikanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PendidikanQuery getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pendidikan not found"));
    }

    public List<PendidikanQuery> listQuery() {
        return queries.listQuery();
    }
}
