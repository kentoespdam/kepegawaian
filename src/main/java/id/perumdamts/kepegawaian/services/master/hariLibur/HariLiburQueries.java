package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburIndexQuery;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburQuery;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HariLiburQueries {
    private final HariLiburRepository repository;

    public Page<HariLiburQuery> pageQuery(HariLiburIndexQuery query) {
        var pageable = PageRequest.of(query.getPage(), query.getSize());
        var page = repository.findAll(query.getSpecification(), pageable);
        var content = page.getContent().stream()
                .map(this::toQuery)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public Optional<HariLiburQuery> getById(Long id) {
        return repository.findById(id).map(this::toQuery);
    }

    public List<HariLiburQuery> listQuery() {
        return repository.findAll().stream()
                .map(this::toQuery)
                .toList();
    }

    private HariLiburQuery toQuery(HariLibur entity) {
        var query = new HariLiburQuery();
        query.setId(entity.getId());
        query.setTanggal(entity.getTanggal());
        query.setJenisLibur(entity.getJenisLibur().name());
        query.setNotes(entity.getNotes());
        return query;
    }
}
