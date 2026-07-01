package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PelatihanDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PelatihanQueryRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PelatihanQueryService {
    private final PelatihanQueryRepository queries;
    private final PelatihanDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;

    public Page<PelatihanQuery> pageQuery(PelatihanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PelatihanDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pelatihan not found"));
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
    }
}