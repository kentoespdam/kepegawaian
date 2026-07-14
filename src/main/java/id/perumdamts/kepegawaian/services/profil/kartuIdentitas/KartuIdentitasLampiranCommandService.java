package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasLampiranPostRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KartuIdentitasLampiranCommandService {
    private static final String UNKNOWN_KARTU_IDENTITAS = "Unknown Kartu Identitas";

    private final KartuIdentitasRepository repository;
    private final LampiranProfilCommandService lampiranProfilCommandService;

    @Transactional
    public Long addLampiran(KartuIdentitasLampiranPostRequest request) {
        if (!repository.existsById(request.getRefId()))
            throw new NotFoundException(UNKNOWN_KARTU_IDENTITAS);
        lampiranProfilCommandService.addLampiran(request);
        return request.getRefId();
    }

    @Transactional
    public boolean deleteLampiran(Long id) {
        lampiranProfilCommandService.deleteById(id);
        return true;
    }
}
