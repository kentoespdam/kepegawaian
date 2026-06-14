package id.perumdamts.kepegawaian.services.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisKitasQueryService {
    private final JenisKitasQueries queries;

    public Page<JenisKitasQuery> pageQuery(JenisKitasIndexQuery query) {
        return queries.pageQuery(query);
    }

    public JenisKitasQuery getById(Long id) {
        return queries.getById(id)
                .orElseThrow(() -> new NotFoundException("JenisKitas not found"));
    }

    public List<JenisKitasQuery> listQuery() {
        return queries.listQuery();
    }
}
