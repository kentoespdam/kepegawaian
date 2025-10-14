package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupCutiJenis implements SetupMaster {
    private final CutiJenisRepository repository;

    @Override
    public void insertBatch() {
        List<CutiJenis> list = new ArrayList<>();

        list.add(new CutiJenis(null, "Cuti tahunan", 12, true));
        list.add(new CutiJenis(null, "Cuti besar", 0, false));
        list.add(new CutiJenis(null, "Cuti sakit", 0, false));
        list.add(new CutiJenis(null, "Cuti melaksanakan ibadah", 0, false));
        list.add(new CutiJenis(null, "Cuti karena alasan penting", 0, false));
        list.add(new CutiJenis(null, "Cuti bersalin", 0, false));
        list.add(new CutiJenis(null, "Cuti di luar tanggungan perusahan", 0, false));
        list.add(new CutiJenis(new CutiJenis(4L), "Menunaikan ibadah haji", 45, false));
        list.add(new CutiJenis(new CutiJenis(4L), "Menunaikan ibadah umroh", 15, false));
        list.add(new CutiJenis(new CutiJenis(4L), "Menunaikan ibadah lainnya", 45, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Menikah", 3, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Menikahkan anak", 2, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Mengkhitankan anak", 2, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Membaptiskan anak", 2, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Istri melahirkan atau keguguran kandungan", 2, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Suami/istri, orang tua/mertua, anak/menantu meninggal dunia", 2, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Anggota keluarga dalam satu rumah meninggal dunia", 1, false));
        list.add(new CutiJenis(new CutiJenis(5L), "Saudara kandung/ipar/tiri/angkat meninggal dunia", 1, false));

        repository.saveAll(list);
    }
}
