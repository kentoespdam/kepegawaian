package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPotonganTkkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGajiPotonganTkk implements SetupMaster {
    private final GajiPotonganTkkRepository repository;

    @Override
    public void insertBatch() {
        List<GajiPotonganTkk> list = new ArrayList<>();
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(2L), 227500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(3L), 204750D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(4L), 204750D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(5L), 113500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(6L), 100000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(1L), 75000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(2L), 75000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(3L), 75000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(4L), 75000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(5L), 80500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(6L), 80500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(7L), 80500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(8L), 80500D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(9L), 86000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(10L), 86000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(11L), 86000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(12L), 86000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.KONTRAK, 0D));
        list.add(new GajiPotonganTkk(EStatusPegawai.CAPEG, 0D));
        list.add(new GajiPotonganTkk(EStatusPegawai.HONORER, 75000D));
        list.add(new GajiPotonganTkk(EStatusPegawai.CALON_HONORER, 0D));

        repository.saveAll(list);
    }
}
