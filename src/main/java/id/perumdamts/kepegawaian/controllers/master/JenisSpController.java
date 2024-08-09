package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisSp;
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
@RequestMapping("/master/jenis-sp")
public class JenisSpController {
    @GetMapping
    public ResponseEntity<?> index() {
        List<Map<String, Object>> list = Arrays.stream(EJenisSp.values()).map(eJenisSp -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", eJenisSp);
            map.put("nama", eJenisSp.value);
            return map;
        }).toList();
        return CustomResult.list(list);
    }
}
