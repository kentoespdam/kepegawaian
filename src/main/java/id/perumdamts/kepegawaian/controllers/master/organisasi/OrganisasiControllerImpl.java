package id.perumdamts.kepegawaian.controllers.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.services.master.organisasi.OrganisasiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class OrganisasiControllerImpl implements OrganisasiController {
    private final OrganisasiService service;

    @Override
    public Mono<ResponseEntity<?>> index() {
        List<Organisasi> all = service.findAll();
        if (all.isEmpty())
            return Mono.just(ResponseEntity.noContent().build());
        return Mono.just(ResponseEntity.ok(all));
    }

    @Override
    public Mono<ResponseEntity<?>> show(Long id) {
        Organisasi byId = service.findById(id);
        return Objects.isNull(byId) ?
                Mono.just(ResponseEntity.notFound().build()) :
                Mono.just(ResponseEntity.ok(byId));
    }

    @Override
    public Mono<ResponseEntity<?>> create(OrganisasiPostRequest request) {
        Organisasi organisasi = service.save(request);
        if (Objects.nonNull(organisasi))
            return Mono.just(ResponseEntity.ok(organisasi));
        return Mono.just(ResponseEntity.badRequest().build());
    }
}
