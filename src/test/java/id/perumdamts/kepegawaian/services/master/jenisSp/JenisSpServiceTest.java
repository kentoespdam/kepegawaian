package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
class JenisSpServiceTest {
    @Mock
    private JenisSpRepository repository;
    //    @Mock
//    private SanksiRepository sanksiRepository;
    private final List<JenisSp> jenisSpList = new ArrayList<>();


    @BeforeEach
    void setup() {
//        List<JenisSp> jenisSpList = new ArrayList<>();
        jenisSpList.add(new JenisSp("TG-LISAN", "Teguran Lisan", Set.of(new Sanksi("S1", "Potongan TKK 1 Hari"))));
        jenisSpList.add(new JenisSp("SP1", "Surat Peringatan Kesatu", Set.of(new Sanksi("S2", "Potongan TKK 2 Hari"))));
        jenisSpList.add(new JenisSp("SP2", "Surat Peringatan Kedua", Set.of(new Sanksi("S3", "Potongan TKK 3 Hari"))));
        jenisSpList.add(new JenisSp("SP3", "Surat Peringatan Ketiga", Set.of(
                new Sanksi("S4", "Potongan TKK 1 Bulan + Penurunan Pangkat"),
                new Sanksi("S5", "Potongan TKK 1 Bulan + Demosi"),
                new Sanksi("S6", "Pemberhentian Sementara"),
                new Sanksi("S7", "Pemberhentian Dengan Hormat"),
                new Sanksi("S8", "Pemberhentian Dengan Tidak Hormat")
        )));
        repository.saveAll(jenisSpList);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(jenisSpList);
        List<JenisSp> all = repository.findAll();
        assertEquals(3, all.size());
        log.info("{}", all.stream().map(JenisSpResponse::from).toList());
    }

}