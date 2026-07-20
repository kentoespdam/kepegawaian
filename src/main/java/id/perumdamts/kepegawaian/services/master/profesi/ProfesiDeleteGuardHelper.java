package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.repositories.master.jpa.ApdRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlatKerjaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfesiDeleteGuardHelper {
    private final ApdRepository apdRepository;
    private final AlatKerjaRepository alatKerjaRepository;

    public void verifyNoActiveChildren(Long profesiId) {
        if (apdRepository.existsByProfesiIdAndIsDeletedFalse(profesiId)) {
            throw new ConflictException("Profesi masih memiliki APD/Alat Kerja");
        }
        if (alatKerjaRepository.existsByProfesiIdAndIsDeletedFalse(profesiId)) {
            throw new ConflictException("Profesi masih memiliki APD/Alat Kerja");
        }
    }
}
