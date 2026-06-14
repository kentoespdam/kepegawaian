package id.perumdamts.kepegawaian.services.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.JenisPelatihanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisPelatihanQueryService {
    private final JenisPelatihanQueryRepository queries;

    public Page<JenisPelatihanQuery> pageQuery(JenisPelatihanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public JenisPelatihanQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("JenisPelatihan not found"));
    }

    public List<JenisPelatihanQuery> listQuery() {
        return queries.listQuery();
    }
}
