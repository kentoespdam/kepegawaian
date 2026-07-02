package id.perumdamts.kepegawaian.services.master.jenisSk;

import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JenisSkQueryService {
    public List<JenisSkResponse> findAll() {
        return Arrays.stream(EJenisSk.values())
                .map(e -> new JenisSkResponse(e.name(), e.value))
                .toList();
    }
}
