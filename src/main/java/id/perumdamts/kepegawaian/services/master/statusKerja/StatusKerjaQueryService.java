package id.perumdamts.kepegawaian.services.master.statusKerja;

import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StatusKerjaQueryService {
    public List<EnumOption> findAll() {
        return Arrays.stream(EStatusKerja.values())
                .map(e -> new EnumOption(e.name(), e.value))
                .toList();
    }
}
