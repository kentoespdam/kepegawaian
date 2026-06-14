package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganIndexQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GolonganQueryService {
    private final GolonganQueries queries;

    public Page<GolonganQuery> pageQuery(GolonganIndexQuery query) {
        return queries.pageQuery(query);
    }

    public GolonganQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Golongan not found"));
    }

    public List<GolonganQuery> listQuery() {
        return queries.listQuery();
    }
}
