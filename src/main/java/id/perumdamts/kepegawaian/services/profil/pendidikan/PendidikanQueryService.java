package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PendidikanDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PendidikanQueryRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendidikanQueryService {
    private final PendidikanQueryRepository queries;
    private final PendidikanDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;

    public Page<PendidikanQuery> pageQuery(PendidikanIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PendidikanQuery getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pendidikan not found"));
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }
}
