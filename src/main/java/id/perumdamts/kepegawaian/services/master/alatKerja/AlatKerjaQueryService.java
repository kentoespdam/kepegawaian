package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.AlatKerjaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlatKerjaQueryService {
    private final AlatKerjaQueryRepository queryRepository;

    public AlatKerjaQuery getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("AlatKerja not found"));
    }
}
