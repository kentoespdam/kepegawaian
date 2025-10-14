package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiTunjanganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGajiTunjangan implements SetupMaster {
    private final GajiTunjanganRepository repository;

    @Override
    public void insertBatch() {
        List<GajiTunjangan> list = new ArrayList<>();
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(5L), 1500000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(6L), 1000000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(1L), 250000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(2L), 250000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(3L), 250000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(4L), 250000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(5L), 275000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(6L), 275000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(7L), 275000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(8L), 275000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(9L), 300000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(10L), 300000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(11L), 300000D));
        list.add(new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(7L), new Golongan(12L), 300000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(5L), 2500000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(6L), 2200000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(1L), 1650000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(2L), 1650000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(3L), 1650000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(4L), 1650000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(5L), 1775000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(6L), 1775000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(7L), 1775000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(8L), 1775000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(9L), 1900000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(10L), 1900000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(11L), 1900000D));
        list.add(new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(12L), 1900000D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(1L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(2L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(3L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(4L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(5L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(6L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(7L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(8L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(9L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(10L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(11L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(12L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(13L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(14L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(15L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(16L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.BERAS, new Level(7L), new Golongan(17L), 72420D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(5L), 150000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(6L), 125000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(1L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(2L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(3L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(4L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(5L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(6L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(7L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(8L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(9L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(10L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(11L), 100000D));
        list.add(new GajiTunjangan(EJenisTunjangan.AIR, new Level(7L), new Golongan(12L), 100000D));

        repository.saveAll(list);
    }
}
