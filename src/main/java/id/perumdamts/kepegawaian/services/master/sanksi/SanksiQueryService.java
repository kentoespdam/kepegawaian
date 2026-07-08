package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiJenisSpList;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.SanksiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanksiQueryService {
    private final SanksiQueryRepository queries;

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

    public List<SanksiJenisSpList> findJenisSpList(Long jenisSpId) {
        return queries.findJenisSpList(jenisSpId);
    }
}
