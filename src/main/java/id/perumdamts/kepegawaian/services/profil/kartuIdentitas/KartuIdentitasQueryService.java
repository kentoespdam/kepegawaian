package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KartuIdentitasDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.KartuIdentitasQueryRepository;
import id.perumdamts.kepegawaian.services.profil.OwnershipGuard;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KartuIdentitasQueryService {
    private final KartuIdentitasQueryRepository queries;
    private final KartuIdentitasDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;
    private final OwnershipGuard ownershipGuard;

    public Page<KartuIdentitasQuery> pageQuery(KartuIdentitasIndexQuery query) {
        String scope = ownershipGuard.readScopeNik();
        if (scope != null) query.setBiodataId(scope);
        return queries.pageQuery(query);
    }

    public KartuIdentitasDetail getById(Long id) {
        KartuIdentitasDetail result = detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Kartu Identitas not found"));
        ownershipGuard.assertSelfRead(result.biodataId());
        return result;
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.KARTU_IDENTITAS, id);
    }
}
