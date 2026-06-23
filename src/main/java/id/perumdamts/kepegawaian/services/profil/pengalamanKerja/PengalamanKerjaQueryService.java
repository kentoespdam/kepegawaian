package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PengalamanKerjaDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.PengalamanKerjaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PengalamanKerjaQueryService {
    private final PengalamanKerjaQueryRepository queries;
    private final PengalamanKerjaDetailQuery detail;

    public Page<PengalamanKerjaQuery> pageQuery(PengalamanKerjaIndexQuery query) {
        return queries.pageQuery(query);
    }

    public PengalamanKerjaDetail getById(Long id) {
        return detail.getById(id)
                .orElseThrow(() -> new NotFoundException("Pengalaman Kerja not found"));
    }
}
