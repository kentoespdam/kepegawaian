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
                .map(eJenisPegawai -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", eJenisPegawai);
                    map.put("nama", eJenisPegawai.value);
                    return map;
                }).toList();

        return CustomResult.list(list);
    }
}
