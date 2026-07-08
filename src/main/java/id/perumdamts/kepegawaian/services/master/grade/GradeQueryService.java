package id.perumdamts.kepegawaian.services.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradeIndexQuery;
import id.perumdamts.kepegawaian.dto.master.grade.GradeListResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.GradeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeQueryService {
    private final GradeQueryRepository queries;

    public Page<GradeQuery> pageQuery(GradeIndexQuery query) {
        return queries.pageQuery(query);
    }

    public GradeQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found"));
    }

    public List<GradeListResponse> listQuery() {
        return queries.listQuery();
    }

    public List<GradeQuery> findByLevelId(Long levelId) {
        return queries.findByLevelId(levelId);
    }
}
