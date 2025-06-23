package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupCutiJenis implements SetupMaster {
    @Autowired
    private CutiJenisRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<CutiJenis> list = new ArrayList<>();

        list.add(new CutiJenis(1L, null, "Cuti tahunan", 12, true));
        list.add(new CutiJenis(2L, null, "Cuti besar", 0, false));
        list.add(new CutiJenis(3L, null, "Cuti sakit", 0, false));
        list.add(new CutiJenis(4L, null, "Cuti melaksanakan ibadah", 0, false));
        list.add(new CutiJenis(5L, null, "Cuti karena alasan penting", 0, false));
        list.add(new CutiJenis(6L, null, "Cuti bersalin", 0, false));
        list.add(new CutiJenis(7L, null, "Cuti di luar tanggungan perusahan", 0, false));
        list.add(new CutiJenis(8L, new CutiJenis(4L), "Menunaikan ibadah haji", 45, false));
        list.add(new CutiJenis(9L, new CutiJenis(4L), "Menunaikan ibadah umroh", 15, false));
        list.add(new CutiJenis(10L, new CutiJenis(4L), "Menunaikan ibadah lainnya", 45, false));
        list.add(new CutiJenis(11L, new CutiJenis(5L), "Menikah", 3, false));
        list.add(new CutiJenis(12L, new CutiJenis(5L), "Menikahkan anak", 2, false));
        list.add(new CutiJenis(13L, new CutiJenis(5L), "Mengkhitankan anak", 2, false));
        list.add(new CutiJenis(14L, new CutiJenis(5L), "Membaptiskan anak", 2, false));
        list.add(new CutiJenis(15L, new CutiJenis(5L), "Istri melahirkan atau keguguran kandungan", 2, false));
        list.add(new CutiJenis(16L, new CutiJenis(5L), "Suami/istri, orang tua/mertua, anak/menantu meninggal dunia", 2, false));
        list.add(new CutiJenis(17L, new CutiJenis(5L), "Anggota keluarga dalam satu rumah meninggal dunia", 1, false));
        list.add(new CutiJenis(18L, new CutiJenis(5L), "Saudara kandung/ipar/tiri/angkat meninggal dunia", 1, false));

        repository.saveAll(list);
    }
}
