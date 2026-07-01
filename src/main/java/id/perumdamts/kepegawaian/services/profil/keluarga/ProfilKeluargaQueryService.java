package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.ProfilKeluargaDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.ProfilKeluargaQueryRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaQueryService {
    private final ProfilKeluargaQueryRepository queries;
    private final ProfilKeluargaDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;

    public Page<ProfilKeluargaQuery> pageQuery(ProfilKeluargaIndexQuery query) {
        return queries.pageQuery(query);
    }

    public ProfilKeluargaQuery getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Profil Keluarga not found"));
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }
}