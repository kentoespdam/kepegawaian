package id.perumdamts.kepegawaian.services.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.AlasanBerhentiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlasanBerhentiQueryService {
    private final AlasanBerhentiQueryRepository queries;

    public Page<AlasanBerhentiQuery> pageQuery(AlasanBerhentiIndexQuery query) {
        return queries.pageQuery(query);
    }

    public AlasanBerhentiQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("AlasanBerhenti not found"));
    }

    public List<AlasanBerhentiQuery> listQuery() {
        return queries.listQuery();
    }
}