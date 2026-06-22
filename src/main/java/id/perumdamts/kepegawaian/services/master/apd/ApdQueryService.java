package id.perumdamts.kepegawaian.services.master.apd;

import id.perumdamts.kepegawaian.dto.master.apd.ApdQuery;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jooq.ApdQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApdQueryService {
    private final ApdQueryRepository queryRepository;

    public ApdQuery getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Apd not found"));
    }
}
