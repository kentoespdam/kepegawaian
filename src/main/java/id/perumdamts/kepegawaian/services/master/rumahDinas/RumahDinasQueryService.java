package id.perumdamts.kepegawaian.services.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasListResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.RumahDinasQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RumahDinasQueryService {
    private final RumahDinasQueryRepository queries;

    public Page<RumahDinasQuery> pageQuery(RumahDinasIndexQuery query) {
        return queries.pageQuery(query);
    }

    public RumahDinasQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("RumahDinas not found"));
    }

    public List<RumahDinasListResponse> listQuery() {
        return queries.listQuery();
    }
}
