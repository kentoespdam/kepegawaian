package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan.GajiTunjanganCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan.GajiTunjanganQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/penggajian/tunjangan")
@RequiredArgsConstructor
public class GajiTunjanganController {
    private final GajiTunjanganCommandService commandService;
    private final GajiTunjanganQueryService queryService;

    @GetMapping
    public ResponseEntity<ListResult<Map<String, Object>>> index() {
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
    public ResponseEntity<PageResult<Page<GajiTunjanganResponse>>> index(@PathVariable EJenisTunjangan jenis, @ParameterObject @Valid GajiTunjanganIndexQuery request) {
        request.setJenis(jenis);
        return CustomResult.page(queryService.findPage(jenis, request));
    }

    @GetMapping("/{jenis}/{id}")
    public ResponseEntity<SingleResult<GajiTunjanganResponse>> show(@PathVariable EJenisTunjangan jenis, @PathVariable Long id) {
        return CustomResult.any(queryService.findById(jenis, id).orElse(null));
    }

    @PostMapping("/{jenis}")
    public ResponseEntity<SavedResult<Long>> save(@PathVariable EJenisTunjangan jenis, @Valid @RequestBody GajiTunjanganPostRequest request) {
        return CustomResult.save(commandService.save(jenis, request));
    }

    @PutMapping("/{jenis}/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable EJenisTunjangan jenis, @PathVariable Long id, @Valid @RequestBody GajiTunjanganPutRequest request) {
        return CustomResult.save(commandService.update(jenis, id, request));
    }

    @DeleteMapping("/{jenis}/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable EJenisTunjangan jenis, @PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(jenis, id));
    }
}
