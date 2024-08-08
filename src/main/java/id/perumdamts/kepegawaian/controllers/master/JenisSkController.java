package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
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
@RequestMapping("/master/jenis-sk")
public class JenisSkController {
    @GetMapping
    public ResponseEntity<?> index() {
        List<Map<String, Object>> list = Arrays.stream(EJenisSk.values()).map(eJenisSk -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", eJenisSk);
            map.put("nama", eJenisSk.value);
            return map;
        }).toList();
        return CustomResult.list(list);
    }
}
