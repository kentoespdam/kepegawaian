package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
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
@RequestMapping("/master/jenis-mutasi")
public class JenisMutasiController {

    @GetMapping
    public ResponseEntity<?> index() {
        List<Map<String, Object>> list = Arrays.stream(EJenisMutasi.values()).map(eJenisMutasi -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", eJenisMutasi);
            data.put("nama", eJenisMutasi.value);
            return data;
        }).toList();
        return CustomResult.list(list);
    }
}
