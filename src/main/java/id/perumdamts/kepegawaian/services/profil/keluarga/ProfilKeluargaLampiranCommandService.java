package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaLampiranPostRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaLampiranCommandService {
    private static final String UNKNOWN_KELUARGA = "Unknown Profil Keluarga";

    private final ProfilKeluargaRepository repository;
    private final LampiranProfilCommandService lampiranProfilCommandService;

    @Transactional
    public Long addLampiran(ProfilKeluargaLampiranPostRequest request, boolean requiresApproval) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_KELUARGA);
        lampiranProfilCommandService.addLampiran(request, requiresApproval);
        return request.getRefId();
    }

    @Transactional
    public boolean deleteLampiran(Long id, boolean requiresApproval) {
        lampiranProfilCommandService.deleteById(id, requiresApproval);
        return true;
    }
}
