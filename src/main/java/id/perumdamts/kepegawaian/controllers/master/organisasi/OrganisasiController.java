package id.perumdamts.kepegawaian.controllers.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping("/master/organisasi")
public interface OrganisasiController {
    @GetMapping
    Mono<ResponseEntity<?>> index();
    @GetMapping("/{id}")
    Mono<ResponseEntity<?>> show(Long id);
    @PostMapping
    Mono<ResponseEntity<?>> create(@RequestBody OrganisasiPostRequest request);
}
