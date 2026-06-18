package id.perumdamts.kepegawaian.services.master.level;

import id.perumdamts.kepegawaian.dto.master.level.LevelIndexQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.LevelQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelQueryService {
    private final LevelQueryRepository queryRepository;

    public Page<LevelQuery> pageQuery(LevelIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public LevelQuery getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Level not found"));
    }

    public List<LevelQuery> listQuery() {
        return queryRepository.listQuery();
    }
}
