package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.OrganisasiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisasiQueryService {
    private final OrganisasiQueryRepository queries;

    public Page<OrganisasiQuery> pageQuery(OrganisasiIndexQuery query) {
        return queries.pageQuery(query);
    }

    public OrganisasiQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
    }

    public List<OrganisasiQuery> listQuery() {
        return queries.listQuery();
    }

    public List<OrganisasiQuery> findByParentId(Long parentId) {
        return queries.findByParentId(parentId);
    }
}
