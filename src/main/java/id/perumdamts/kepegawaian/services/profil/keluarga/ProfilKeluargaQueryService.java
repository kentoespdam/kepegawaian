package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.ProfilKeluargaDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.ProfilKeluargaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaQueryService {
    private final ProfilKeluargaQueryRepository queries;
    private final ProfilKeluargaDetailQuery detail;

    public Page<ProfilKeluargaQuery> pageQuery(ProfilKeluargaIndexQuery query) {
        return queries.pageQuery(query);
    }

    public ProfilKeluargaQuery getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Profil Keluarga not found"));
    }
}