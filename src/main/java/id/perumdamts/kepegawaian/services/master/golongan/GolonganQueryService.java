package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganIndexQuery;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.GolonganQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GolonganQueryService {
    private final GolonganQueryRepository queryRepository;

    public Page<GolonganQuery> pageQuery(GolonganIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public GolonganQuery getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Golongan not found"));
    }

    public List<GolonganQuery> listQuery() {
        return queryRepository.listQuery();
    }
}
