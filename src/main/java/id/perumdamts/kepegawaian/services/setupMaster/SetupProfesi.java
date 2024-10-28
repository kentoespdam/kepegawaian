package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupProfesi implements SetupMaster {
    @Autowired
    private ProfesiRepository profesiRepository;

    @Override
    public void insertBatch() {
        List<Profesi> list = new ArrayList<>();
        list.add(new Profesi(1L, new Level(5L),"Manajer Perencanaan & Pengembangan ","-","-"));
        list.add(new Profesi(2L, new Level(5L),"Manajer Produksi & Distribusi 1","-","-"));
        list.add(new Profesi(3L, new Level(5L),"Manajer Pengendalian Teknik","-","-"));
        list.add(new Profesi(4L, new Level(5L),"Manajer Keuangan","-","-"));
        list.add(new Profesi(5L, new Level(5L),"Manajer Kesekretariatan","-","-"));
        list.add(new Profesi(6L, new Level(5L),"Manajer Sumber Daya Manusia & TI","-","-"));
        list.add(new Profesi(7L, new Level(5L),"Manajer Cabang Purwokerto 1","-","-"));
        list.add(new Profesi(8L, new Level(5L),"Manajer Perlengkapan","-","-"));
        list.add(new Profesi(9L, new Level(5L),"Manajer Unit Bisnis AMDK","-","-"));
        list.add(new Profesi(10L, new Level(5L),"Manajer SPI","-","-"));
        list.add(new Profesi(11L, new Level(5L),"Manajer Produksi & Distribusi 2","-","-"));
        list.add(new Profesi(12L, new Level(5L),"Manajer Bagian Pengendalian Teknik","-","-"));
        list.add(new Profesi(13L, new Level(5L),"Manajer Cabang Purwokerto 2","-","-"));
        list.add(new Profesi(14L, new Level(5L),"Manajer Cabang Ajibarang","-","-"));
        list.add(new Profesi(15L, new Level(5L),"Manajer Cabang Wangon","-","-"));
        list.add(new Profesi(16L, new Level(6L),"Supervisor Satuan Pengawas Intern","-","-"));
        list.add(new Profesi(17L, new Level(6L),"Supervisor Bagian Perencanaan & Pengembangan","-","-"));
        list.add(new Profesi(18L, new Level(6L),"Supervisor Bagian Produksi & Distribusi 1","-","-"));
        list.add(new Profesi(19L, new Level(6L),"Supervisor Bagian Produksi & Distribusi 2","-","-"));
        list.add(new Profesi(20L, new Level(6L),"Supervisor Bagian Pengendalian Teknik","-","-"));
        list.add(new Profesi(21L, new Level(6L),"Supervisor Bagian Kesekretariatan","-","-"));
        list.add(new Profesi(22L, new Level(6L),"Supervisor Bagian Sumber Daya Manusia & TI","-","-"));
        list.add(new Profesi(23L, new Level(6L),"Supervisor Bagian Perlengkapan","-","-"));
        list.add(new Profesi(24L, new Level(6L),"Supervisor Bagian Adm. & Keuangan Cabang","-","-"));
        list.add(new Profesi(25L, new Level(6L),"Supervisor Bagian Pelayanan Cabang","-","-"));
        list.add(new Profesi(26L, new Level(6L),"Supervisor Bagian Teknik Cabang","-","-"));
        list.add(new Profesi(27L, new Level(6L),"Supervisor Unit Bisnis AMDK","-","-"));
        list.add(new Profesi(28L, new Level(6L),"Supervisor Bagian Keuangan","-","-"));
        list.add(new Profesi(29L, new Level(7L),"Staff Satuan Pengawas Intern","-","-"));
        list.add(new Profesi(30L, new Level(7L),"Staf Bagian Perencanaan & Pengembangan","-","-"));
        list.add(new Profesi(31L, new Level(7L),"Staf Bagian Produksi & Distribusi (administrasi)","-","-"));
        list.add(new Profesi(32L, new Level(7L),"Staff Sub Bag Pengendalian Distribusi (teknik)","-","-"));
        list.add(new Profesi(33L, new Level(7L),"Staf Bagian Produksi & Distribusi dengan tugas pengendalian produksi pada mata air dan pompa","-","-"));
        list.add(new Profesi(34L, new Level(7L),"Staf Bagian Produksi & Distribusi dengan tugas di unit IPA Gunung Tugel","-","-"));
        list.add(new Profesi(35L, new Level(7L),"Staf Bagian Produksi & Distribusi dengan tugas di unit IPA Sidabowa, IPA Kemranjen, IPA Kejawar dan IPA Kaliori","-","-"));
        list.add(new Profesi(36L, new Level(7L),"Staf Sub Bag Penjaminan Mutu Air","-","-"));
        list.add(new Profesi(37L, new Level(7L),"Staf Sub Bag Mekanikal & Elektrikal","-","-"));
        list.add(new Profesi(38L, new Level(7L),"Staf Sub Bag Pengendalian Kehilangan Air","-","-"));
        list.add(new Profesi(39L, new Level(7L),"Staf Sub Bag Mekanikal & Elektrikal (Admin)","-","-"));
        list.add(new Profesi(40L, new Level(7L),"Staff Sub Bag pada Bagian Kesekretariatan","-","-"));
        list.add(new Profesi(41L, new Level(7L),"Staf Sub Bag Akuntansi","-","-"));
        list.add(new Profesi(42L, new Level(7L),"Staf Sub Bag Anggaran & Pelaporan","-","-"));
        list.add(new Profesi(43L, new Level(7L),"Staf Sub Bag Perbendaharaan","-","-"));
        list.add(new Profesi(44L, new Level(7L),"Staf Bagian Sumber Daya Manusia & TI","-","-"));
        list.add(new Profesi(45L, new Level(7L),"Staf Sub Bag Teknologi Informasi (administrasi)","-","-"));
        list.add(new Profesi(46L, new Level(7L),"Staf Sub Bag Teknologi Informasi (programmer)","-","-"));
        list.add(new Profesi(47L, new Level(7L),"Staf Sub Bag Teknologi Informasi (Performa Server, Hardware, jaringan & Mikrotik)","-","-"));
        list.add(new Profesi(48L, new Level(7L),"Staf Sub Bag pada Bagian Perlengkapan","-","-"));
        list.add(new Profesi(49L, new Level(7L),"Staf Sub Bag Adm. & Keuangan Cabang","-","-"));
        list.add(new Profesi(50L, new Level(7L),"Staf Sub Bag Pelayanan Cabang","-","-"));
        list.add(new Profesi(51L, new Level(7L),"Staf Sub Bag AMDK (Admin)","-","-"));
        list.add(new Profesi(52L, new Level(7L),"Staf Sub Bag AMDK","-","-"));
        list.add(new Profesi(53L, new Level(7L),"Kasir","-","-"));
        list.add(new Profesi(54L, new Level(7L),"Staf Sub Bag Teknik Cabang (PTM)","-","-"));
        list.add(new Profesi(55L, new Level(7L),"Staf Sub Bag Teknik Cabang (PMS)","-","-"));
        list.add(new Profesi(56L, new Level(7L),"Staf Sub Bag Teknik Cabang (PJ)","-","-"));
        list.add(new Profesi(57L, new Level(7L),"Staf Sub Bag Teknik Cabang (Adm RAB)","-","-"));
        list.add(new Profesi(58L, new Level(7L),"Staf Sub Bag Teknik Cabang (Adm Kegiatan Teknik)","-","-"));
        list.add(new Profesi(59L, new Level(5L),"Manajer Cabang Banyumas","-","-"));

        profesiRepository.saveAll(list);
    }
}
