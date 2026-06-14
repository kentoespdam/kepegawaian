package id.perumdamts.kepegawaian.services.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.JenisKeahlianQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisKeahlianQueryService {
    private final JenisKeahlianQueryRepository queries;

    public Page<JenisKeahlianQuery> pageQuery(JenisKeahlianIndexQuery query) {
        return queries.pageQuery(query);
    }

    public JenisKeahlianQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("JenisKeahlian not found"));
    }

    public List<JenisKeahlianQuery> listQuery() {
        return queries.listQuery();
    }
}
