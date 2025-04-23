package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiTunjanganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupGajiTunjangan implements SetupMaster {
    @Autowired
    private GajiTunjanganRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<GajiTunjangan> list = new ArrayList<>();
        list.add(new GajiTunjangan(1L, EJenisTunjangan.JABATAN, new Level(5L), 1500000D));
        list.add(new GajiTunjangan(2L, EJenisTunjangan.JABATAN, new Level(6L), 1000000D));
        list.add(new GajiTunjangan(3L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(1L), 250000D));
        list.add(new GajiTunjangan(4L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(2L), 250000D));
        list.add(new GajiTunjangan(5L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(3L), 250000D));
        list.add(new GajiTunjangan(6L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(4L), 250000D));
        list.add(new GajiTunjangan(7L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(5L), 275000D));
        list.add(new GajiTunjangan(8L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(6L), 275000D));
        list.add(new GajiTunjangan(9L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(7L), 275000D));
        list.add(new GajiTunjangan(10L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(8L), 275000D));
        list.add(new GajiTunjangan(11L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(9L), 300000D));
        list.add(new GajiTunjangan(12L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(10L), 300000D));
        list.add(new GajiTunjangan(13L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(11L), 300000D));
        list.add(new GajiTunjangan(14L, EJenisTunjangan.JABATAN, new Level(7L), new Golongan(12L), 300000D));
        list.add(new GajiTunjangan(15L, EJenisTunjangan.KINERJA, new Level(5L), 2500000D));
        list.add(new GajiTunjangan(16L, EJenisTunjangan.KINERJA, new Level(6L), 2200000D));
        list.add(new GajiTunjangan(17L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(1L), 1650000D));
        list.add(new GajiTunjangan(18L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(2L), 1650000D));
        list.add(new GajiTunjangan(19L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(3L), 1650000D));
        list.add(new GajiTunjangan(20L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(4L), 1650000D));
        list.add(new GajiTunjangan(21L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(5L), 1775000D));
        list.add(new GajiTunjangan(22L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(6L), 1775000D));
        list.add(new GajiTunjangan(23L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(7L), 1775000D));
        list.add(new GajiTunjangan(24L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(8L), 1775000D));
        list.add(new GajiTunjangan(25L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(9L), 1900000D));
        list.add(new GajiTunjangan(26L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(10L), 1900000D));
        list.add(new GajiTunjangan(27L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(11L), 1900000D));
        list.add(new GajiTunjangan(28L, EJenisTunjangan.KINERJA, new Level(7L), new Golongan(12L), 1900000D));
        list.add(new GajiTunjangan(29L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(1L), 72420D));
        list.add(new GajiTunjangan(30L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(2L), 72420D));
        list.add(new GajiTunjangan(31L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(3L), 72420D));
        list.add(new GajiTunjangan(32L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(4L), 72420D));
        list.add(new GajiTunjangan(33L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(5L), 72420D));
        list.add(new GajiTunjangan(34L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(6L), 72420D));
        list.add(new GajiTunjangan(35L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(7L), 72420D));
        list.add(new GajiTunjangan(36L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(8L), 72420D));
        list.add(new GajiTunjangan(37L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(9L), 72420D));
        list.add(new GajiTunjangan(38L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(10L), 72420D));
        list.add(new GajiTunjangan(39L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(11L), 72420D));
        list.add(new GajiTunjangan(40L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(12L), 72420D));
        list.add(new GajiTunjangan(41L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(13L), 72420D));
        list.add(new GajiTunjangan(42L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(14L), 72420D));
        list.add(new GajiTunjangan(43L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(15L), 72420D));
        list.add(new GajiTunjangan(44L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(16L), 72420D));
        list.add(new GajiTunjangan(45L, EJenisTunjangan.BERAS, new Level(7L), new Golongan(17L), 72420D));
        list.add(new GajiTunjangan(46L, EJenisTunjangan.AIR, new Level(5L), 150000D));
        list.add(new GajiTunjangan(47L, EJenisTunjangan.AIR, new Level(6L), 125000D));
        list.add(new GajiTunjangan(48L, EJenisTunjangan.AIR, new Level(7L), new Golongan(1L), 100000D));
        list.add(new GajiTunjangan(49L, EJenisTunjangan.AIR, new Level(7L), new Golongan(2L), 100000D));
        list.add(new GajiTunjangan(50L, EJenisTunjangan.AIR, new Level(7L), new Golongan(3L), 100000D));
        list.add(new GajiTunjangan(51L, EJenisTunjangan.AIR, new Level(7L), new Golongan(4L), 100000D));
        list.add(new GajiTunjangan(52L, EJenisTunjangan.AIR, new Level(7L), new Golongan(5L), 100000D));
        list.add(new GajiTunjangan(53L, EJenisTunjangan.AIR, new Level(7L), new Golongan(6L), 100000D));
        list.add(new GajiTunjangan(54L, EJenisTunjangan.AIR, new Level(7L), new Golongan(7L), 100000D));
        list.add(new GajiTunjangan(55L, EJenisTunjangan.AIR, new Level(7L), new Golongan(8L), 100000D));
        list.add(new GajiTunjangan(56L, EJenisTunjangan.AIR, new Level(7L), new Golongan(9L), 100000D));
        list.add(new GajiTunjangan(57L, EJenisTunjangan.AIR, new Level(7L), new Golongan(10L), 100000D));
        list.add(new GajiTunjangan(58L, EJenisTunjangan.AIR, new Level(7L), new Golongan(11L), 100000D));
        list.add(new GajiTunjangan(59L, EJenisTunjangan.AIR, new Level(7L), new Golongan(12L), 100000D));

        repository.saveAll(list);
    }
}
