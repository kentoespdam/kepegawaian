package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kepegawaian/riwayat-kontrak")
@RequiredArgsConstructor
public class RiwayatKontrakController {
    private final RiwayatKontrakService service;
    ValidatorFactory factory= Validation.buildDefaultValidatorFactory();
    Validator validator=factory.getValidator();

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> index(@PathVariable Long id, RiwayatKontrakRequest request) {
        return CustomResult.any(service.findByPegawaiId(id, request));
    }
}
