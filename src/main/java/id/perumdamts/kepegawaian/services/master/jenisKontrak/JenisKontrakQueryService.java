package id.perumdamts.kepegawaian.services.master.jenisKontrak;

import id.perumdamts.kepegawaian.dto.master.jenisKontrak.JenisKontrakResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JenisKontrakQueryService {
    public List<JenisKontrakResponse> findAll() {
        return Arrays.stream(EJenisKontrak.values())
                .map(e -> new JenisKontrakResponse(e.name(), e.value))
                .toList();
    }
}
