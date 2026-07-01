package id.perumdamts.kepegawaian.services.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.JenjangPendidikanQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenjangPendidikanQueryService {
    private final JenjangPendidikanQueryRepository queryRepository;

    public Page<JenjangPendidikanResponse> pageQuery(JenjangPendidikanIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public JenjangPendidikanResponse getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("JenjangPendidikan not found"));
    }

    public List<JenjangPendidikanResponse> findAll() {
        return queryRepository.listQuery();
    }
}
