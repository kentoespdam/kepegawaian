package id.perumdamts.kepegawaian.services.master.statusKerja;

import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StatusKerjaQueryService {
    public List<StatusKerjaResponse> findAll() {
        return Arrays.stream(EStatusKerja.values())
                .map(e -> new StatusKerjaResponse(e.name(), e.value))
                .toList();
    }
}
