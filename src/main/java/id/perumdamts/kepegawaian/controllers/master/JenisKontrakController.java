package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
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
@RequestMapping("/master/jenis-kontrak")
public class JenisKontrakController {

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> list = Arrays.stream(EJenisKontrak.values())
                .map(eJenisKontrak -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", eJenisKontrak);
                    data.put("nama", eJenisKontrak.value);
                    return data;
                }).toList();
        return CustomResult.list(list);
    }
}
