package id.perumdamts.kepegawaian.services.master.jenisMutasi;

import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JenisMutasiQueryService {
    public List<JenisMutasiResponse> findAll() {
        return Arrays.stream(EJenisMutasi.values())
                .map(e -> new JenisMutasiResponse(e.name(), e.value))
                .toList();
    }
}
