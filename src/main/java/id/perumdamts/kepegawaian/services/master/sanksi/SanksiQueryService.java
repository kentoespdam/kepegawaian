package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanksiQueryService {
    private final SanksiQueries queries;

    public Page<SanksiQuery> pageQuery(SanksiIndexQuery query) {
        return queries.pageQuery(query);
    }

    public SanksiQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Sanksi not found"));
    }

    public List<SanksiQuery> listQuery() {
        return queries.listQuery();
    }

    public List<SanksiQuery> findByJenisSpId(Long jenisSpId) {
        return queries.findByJenisSpId(jenisSpId);
    }
}
