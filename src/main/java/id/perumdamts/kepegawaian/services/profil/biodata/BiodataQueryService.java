package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BiodataQueryService {
    private final BiodataQueryRepository queries;
    private final BiodataDetailQuery detail;

    public Page<BiodataQuery> pageQuery(BiodataIndexQuery query) {
        return queries.pageQuery(query);
    }

    public BiodataDetail getById(String nik) {
        return detail.getById(nik)
                .orElseThrow(() -> new NotFoundException("Biodata not found"));
    }

    public List<BiodataQuery> findAll(BiodataIndexQuery query) {
        return queries.listQuery(query);
    }
}
