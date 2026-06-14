package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.JenisSpQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisSpQueryService {
    private final JenisSpQueryRepository queries;

    public Page<JenisSpQuery> pageQuery(JenisSpIndexQuery query) {
        return queries.pageQuery(query);
    }

    public JenisSpQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Jenis SP not found"));
    }

    public List<JenisSpQuery> listQuery() {
        return queries.listQuery();
    }
}
