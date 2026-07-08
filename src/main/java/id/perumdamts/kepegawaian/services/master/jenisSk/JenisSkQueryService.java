package id.perumdamts.kepegawaian.services.master.jenisSk;

import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JenisSkQueryService {
    public List<EnumOption> findAll() {
        return Arrays.stream(EJenisSk.values())
                .map(e -> new EnumOption(e.name(), e.value))
                .toList();
    }
}
