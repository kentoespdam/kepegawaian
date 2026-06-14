package id.perumdamts.kepegawaian.services.master.statusPegawai;

import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StatusPegawaiServiceImpl implements StatusPegawaiService {
    @Override
    public List<StatusPegawaiResponse> findAll() {
        return Arrays.stream(EStatusPegawai.values())
                .map(e -> new StatusPegawaiResponse(e.name(), e.value, getUrut(e)))
                .toList();
    }

    private Integer getUrut(EStatusPegawai status) {
        return switch (status) {
            case PEGAWAI -> 1;
            case CAPEG -> 2;
            case HONORER -> 3;
            case CALON_HONORER -> 4;
            case KONTRAK -> 5;
            default -> 6;
        };
    }
}
