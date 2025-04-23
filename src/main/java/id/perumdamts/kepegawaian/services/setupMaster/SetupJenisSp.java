package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenisSp implements SetupMaster {
    private final JenisSpRepository repository;


    @Override
    public void insertBatch() throws JsonProcessingException {
        List<JenisSp> jenisSpList = List.of(
                new JenisSp(1L, "TG-LISAN", "Teguran Lisan"),
                new JenisSp(2L, "SP-1", "Surat Peringatan Kesatu"),
                new JenisSp(3L, "SP-2", "Surat Peringatan Kedua"),
                new JenisSp(4L, "SP-3", "Surat Peringatan Ketiga")
        );

        repository.saveAll(jenisSpList);
    }
}
