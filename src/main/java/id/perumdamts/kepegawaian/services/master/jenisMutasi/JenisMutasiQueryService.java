package id.perumdamts.kepegawaian.services.master.jenisMutasi;

import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JenisMutasiQueryService {
    public List<EnumOption> findAll() {
        return Arrays.stream(EJenisMutasi.values())
                .map(e -> new EnumOption(e.name(), e.value))
                .toList();
    }
}
