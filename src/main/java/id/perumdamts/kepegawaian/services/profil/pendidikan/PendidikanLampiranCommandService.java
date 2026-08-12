package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanLampiranPostRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PendidikanLampiranCommandService {
    private static final String UNKNOWN_PENDIDIKAN = "Unknown Pendidikan";

    private final PendidikanRepository repository;
    private final LampiranProfilCommandService lampiranProfilCommandService;

    @Transactional
    public Long addLampiran(PendidikanLampiranPostRequest request, boolean requiresApproval) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_PENDIDIKAN);
        lampiranProfilCommandService.addLampiran(request, requiresApproval);
        return request.getRefId();
    }

    @Transactional
    public boolean deleteLampiran(Long id, boolean requiresApproval) {
        lampiranProfilCommandService.deleteById(id, requiresApproval);
        return true;
    }
}
