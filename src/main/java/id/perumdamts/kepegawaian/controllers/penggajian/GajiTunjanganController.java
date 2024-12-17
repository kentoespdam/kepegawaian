package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan.GajiTunjanganService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/penggajian/tunjangan")
@RequiredArgsConstructor
public class GajiTunjanganController {
    private final GajiTunjanganService service;

    @GetMapping
    public ResponseEntity<?> index() {
        List<Map<String, Object>> list = Arrays.stream(EJenisTunjangan.values())
                .map(eJenisTunjangan -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", eJenisTunjangan);
                    map.put("nama", eJenisTunjangan.value);
                    return map;
                }).toList();
        return CustomResult.list(list);
    }

    @GetMapping("/{jenis}")
    public ResponseEntity<?> index(@PathVariable EJenisTunjangan jenis, @ParameterObject GajiTunjanganRequest request) {
        request.setJenis(jenis);
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/{jenis}/{id}")
    public ResponseEntity<?> show(@PathVariable EJenisTunjangan jenis, @PathVariable Long id) {
        return CustomResult.any(service.findById(jenis, id));
    }

    @PostMapping("/{jenis}")
    public ResponseEntity<?> save(@PathVariable EJenisTunjangan jenis, @Valid @RequestBody GajiTunjanganPostRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.save(service.save(jenis, request));
    }

    @PutMapping("/{jenis}/{id}")
    public ResponseEntity<?> update(@PathVariable EJenisTunjangan jenis, @PathVariable Long id, @Valid @RequestBody GajiTunjanganPutRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.save(service.update(jenis, id, request));
    }

    @DeleteMapping("/{jenis}/{id}")
    public ResponseEntity<?> deleteById(@PathVariable EJenisTunjangan jenis, @PathVariable Long id) {
        return CustomResult.delete(service.deleteById(jenis, id));
    }
}
