package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/status-pegawai")
public class StatusPegawaiController {
    @GetMapping("/list")
    public ResponseEntity<?> index() {
        List<Map<String, Object>> list = Arrays.stream(EStatusPegawai.values())
                .map(statusPegawai -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", statusPegawai);
                    map.put("nama", statusPegawai.value);
                    map.put("urut", getUrut(statusPegawai));
                    return map;
                }).toList();

        return CustomResult.list(list);
    }

    private Integer getUrut(EStatusPegawai statusPegawai){
        return switch (statusPegawai) {
            case PEGAWAI -> 1;
            case CAPEG -> 2;
            case HONORER -> 3;
            case CALON_HONORER -> 4;
            case KONTRAK -> 5;
            default -> 6;
        };
    }
}
