package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisSpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenisSp implements SetupMaster {
    private final JenisSpRepository repository;


    @Override
    public void insertBatch() {
        List<JenisSp> jenisSpList = List.of(
                new JenisSp("TG-LISAN", "Teguran Lisan"),
                new JenisSp("SP-1", "Surat Peringatan Kesatu"),
                new JenisSp("SP-2", "Surat Peringatan Kedua"),
                new JenisSp("SP-3", "Surat Peringatan Ketiga")
        );

        repository.saveAll(jenisSpList);
    }
}
