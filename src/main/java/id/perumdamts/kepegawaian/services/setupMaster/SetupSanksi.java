package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.repositories.master.SanksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupSanksi implements SetupMaster {
    private final SanksiRepository repository;

    @Override
    public void insertBatch() {
        List<Sanksi> sanksiList = List.of(
                new Sanksi("S1", "Potongan TKK 1 Hari", new JenisSp(1L), true, 1, false, false, false, false, false, false, false),
                new Sanksi("S2", "Potongan TKK 2 Hari & Penundaan Gaji Berkala selama 1 Tahun", new JenisSp(2L), true, 2, false, true, false, false, false, false, false),
                new Sanksi("S3", "Potongan TKK 3 Hari & Penundaan Kenaikan Pangkat selama 1 Tahun", new JenisSp(3L), true, 3, true, false, false, false, false, false, false),
                new Sanksi("S4", "Tidak menerima TKK satu bulan dan Penurunan pangkat satu tingkat", new JenisSp(4L), true, 22, false, false, true, false, false, false, false),
                new Sanksi("S5", "Tidak menerima TKK satu bulan dan Penurunan jabatan satu tingkat", new JenisSp(4L), true, 22, false, false, false, true, false, false, false),
                new Sanksi("S6", "Pemberhentian sementara sebagai pegawai", new JenisSp(4L), false, 0, false, false, false, false, true, false, false),
                new Sanksi("S7", "Pemberhentian dengan hormat tidak atas permintaan sendiri sebagai pegawai", new JenisSp(4L), false, 0, false, false, false, false, false, true, false),
                new Sanksi("S8", "Pemberhentian dengan tidak hormat sebagai pegawai", new JenisSp(4L), false, 0, false, false, false, false, false, false, true)
        );
        repository.saveAll(sanksiList);
    }
}
