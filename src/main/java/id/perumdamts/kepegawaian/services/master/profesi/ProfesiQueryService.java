package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.ProfesiDetailQuery;
import id.perumdamts.kepegawaian.repositories.master.jooq.ProfesiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfesiQueryService {
    private final ProfesiQueryRepository queries;
    private final ProfesiDetailQuery detailQuery;

    public Page<ProfesiQuery> pageQuery(ProfesiIndexQuery query) {
        return queries.pageQuery(query);
    }

    public List<ProfesiQuery> listQuery() {
        return queries.listQuery();
    }

    public ProfesiDetail getById(Long id) {
        return detailQuery.getById(id)
                .orElseThrow(() -> new NotFoundException("Profesi not found"));
    }
}
