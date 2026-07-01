package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KeahlianDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KeahlianQueryRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeahlianQueryService {
    private final KeahlianQueryRepository queries;
    private final KeahlianDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;

    public Page<KeahlianQuery> pageQuery(KeahlianIndexQuery query) {
        return queries.pageQuery(query);
    }

    public KeahlianDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Keahlian not found"));
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
    }
}
