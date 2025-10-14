package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.repositories.master.JenisPelatihanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenisPelatihan implements SetupMaster {
    private final JenisPelatihanRepository jenisPelatihanRepository;

    @Override
    public void insertBatch() {
        List<JenisPelatihan> jenisPelatihanList = List.of(
                new JenisPelatihan("Administrasi"),
                new JenisPelatihan("Keuangan"),
                new JenisPelatihan("Pelayanan"),
                new JenisPelatihan("IT"),
                new JenisPelatihan("Perpipaan"),
                new JenisPelatihan("Listrik & Perpompaan"),
                new JenisPelatihan("Pengolahan"),
                new JenisPelatihan("SPAM")
        );
        jenisPelatihanRepository.saveAll(jenisPelatihanList);
    }
}
