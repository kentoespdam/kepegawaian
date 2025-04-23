package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPotonganTkkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupGajiPotonganTkk implements SetupMaster{
    @Autowired
    private GajiPotonganTkkRepository repository;


    @Override
    public void insertBatch() throws JsonProcessingException {
        List<GajiPotonganTkk> list=new ArrayList<>();
        list.add(new GajiPotonganTkk(1L, EStatusPegawai.PEGAWAI, new Level(2L), 227500D));
        list.add(new GajiPotonganTkk(2L, EStatusPegawai.PEGAWAI, new Level(3L), 204750D));
        list.add(new GajiPotonganTkk(3L, EStatusPegawai.PEGAWAI, new Level(4L), 204750D));
        list.add(new GajiPotonganTkk(4L, EStatusPegawai.PEGAWAI, new Level(5L), 113500D));
        list.add(new GajiPotonganTkk(5L, EStatusPegawai.PEGAWAI, new Level(6L), 100000D));
        list.add(new GajiPotonganTkk(6L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(1L), 75000D));
        list.add(new GajiPotonganTkk(7L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(2L), 75000D));
        list.add(new GajiPotonganTkk(8L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(3L), 75000D));
        list.add(new GajiPotonganTkk(9L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(4L), 75000D));
        list.add(new GajiPotonganTkk(10L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(5L), 80500D));
        list.add(new GajiPotonganTkk(11L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(6L), 80500D));
        list.add(new GajiPotonganTkk(12L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(7L), 80500D));
        list.add(new GajiPotonganTkk(13L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(8L), 80500D));
        list.add(new GajiPotonganTkk(14L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(9L), 86000D));
        list.add(new GajiPotonganTkk(15L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(10L), 86000D));
        list.add(new GajiPotonganTkk(16L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(11L), 86000D));
        list.add(new GajiPotonganTkk(17L, EStatusPegawai.PEGAWAI, new Level(7L), new Golongan(12L), 86000D));
        list.add(new GajiPotonganTkk(18L, EStatusPegawai.KONTRAK, 0D));
        list.add(new GajiPotonganTkk(19L, EStatusPegawai.CAPEG, 0D));
        list.add(new GajiPotonganTkk(20L, EStatusPegawai.HONORER, 75000D));
        list.add(new GajiPotonganTkk(21L, EStatusPegawai.CALON_HONORER, 0D));

        repository.saveAll(list);
    }
}
