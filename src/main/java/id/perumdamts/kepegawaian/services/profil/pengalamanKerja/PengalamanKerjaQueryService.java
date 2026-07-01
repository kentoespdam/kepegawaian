package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PengalamanKerjaDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PengalamanKerjaQueryRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PengalamanKerjaQueryService {
    private final PengalamanKerjaQueryRepository queries;
    private final PengalamanKerjaDetailQuery detail;
    private final LampiranProfilQueryService lampiranProfilQueryService;

    public Page<PengalamanKerjaQuery> pageQuery(PengalamanKerjaIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PengalamanKerjaDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pengalaman Kerja not found"));
    }

    public List<LampiranProfilQuery> getLampiran(Long id) {
        return lampiranProfilQueryService.getLampiran(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        return lampiranProfilQueryService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilQueryService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
    }
}
