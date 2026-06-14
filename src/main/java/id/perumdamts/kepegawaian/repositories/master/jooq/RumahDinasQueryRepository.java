package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasQuery;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.repositories.master.jpa.RumahDinasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RumahDinasQueryRepository {
    private final RumahDinasRepository repository;

    public Page<RumahDinasQuery> pageQuery(RumahDinasIndexQuery query) {
        var pageable = PageRequest.of(query.getPage(), query.getSize());
        var page = repository.findAll(query.getSpecification(), pageable);
        var content = page.getContent().stream()
                .map(this::toQuery)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public Optional<RumahDinasQuery> getById(Long id) {
        return repository.findById(id).map(this::toQuery);
    }

    public List<RumahDinasQuery> listQuery() {
        return repository.findAll().stream()
                .map(this::toQuery)
                .toList();
    }

    private RumahDinasQuery toQuery(RumahDinas entity) {
        var query = new RumahDinasQuery();
        query.setId(entity.getId());
        query.setNama(entity.getNama());
        query.setNilai(entity.getNilai());
        return query;
    }
}
