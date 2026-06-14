package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.JabatanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JabatanQueryService {
    private final JabatanQueryRepository queries;

    public Page<JabatanQuery> pageQuery(JabatanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public JabatanQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Jabatan not found"));
    }

    public List<JabatanQuery> listQuery() {
        return queries.listQuery();
    }

    public List<JabatanQuery> findByParentId(Long parentId) {
        return queries.findByParentId(parentId);
    }

    public List<JabatanQuery> findByOrganisasiId(Long organisasiId) {
        return queries.findByOrganisasiId(organisasiId);
    }
}
