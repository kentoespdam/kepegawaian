package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburIndexQuery;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburListResponse;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.HariLiburQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HariLiburQueryService {
    private final HariLiburQueryRepository queries;

    public Page<HariLiburQuery> pageQuery(HariLiburIndexQuery query) {
        return queries.pageQuery(query);
    }

    public HariLiburQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("HariLibur not found"));
    }

    public List<HariLiburListResponse> listQuery() {
        return queries.listQuery();
    }
}
