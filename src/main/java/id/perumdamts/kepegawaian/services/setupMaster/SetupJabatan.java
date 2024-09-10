package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupJabatan implements SetupMaster {
    @Autowired
    private JabatanRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        Jabatan dewas = new Jabatan(1L);
        dewas.setOrganisasi(new Organisasi(1L));
        dewas.setLevel(new Level(1L));
        dewas.setNama("Kepala Dewan Pengawas");

        List<Jabatan> list = new ArrayList<>();
        list.add(dewas);
        list.add(new Jabatan(2L, new Jabatan(1L), new Organisasi(2L), new Level(2L),"Direktur Utama"));
        list.add(new Jabatan(3L, new Jabatan(2L), new Organisasi(40L), new Level(3L),"Direktur Teknik"));
        list.add(new Jabatan(4L, new Jabatan(3L), new Organisasi(41L), new Level(5L),"Manajer Pengendalian Teknik"));
        list.add(new Jabatan(5L, new Jabatan(4L), new Organisasi(42L), new Level(6L),"Supervisor Mekanikal & Elektrikal"));
        list.add(new Jabatan(6L, new Jabatan(5L), new Organisasi(42L), new Level(7L),"Mandor"));
        list.add(new Jabatan(7L, new Jabatan(5L), new Organisasi(42L), new Level(7L),"Staf Sub Bag. Mekanikal & Elektrikal"));
        list.add(new Jabatan(8L, new Jabatan(4L), new Organisasi(43L), new Level(6L),"Supervisor Pengendalian Kehilangan Air"));
        list.add(new Jabatan(9L, new Jabatan(8L), new Organisasi(43L), new Level(7L),"Staf Sub Bag. Pengendalian Kehilangan Air"));
        list.add(new Jabatan(10L, new Jabatan(4L), new Organisasi(44L), new Level(6L),"Supervisor Penjaminan Mutu Air"));
        list.add(new Jabatan(11L, new Jabatan(10L), new Organisasi(44L), new Level(7L),"Staf Sub Bag. Penjaminan Mutu Air"));
        list.add(new Jabatan(12L, new Jabatan(3L), new Organisasi(45L), new Level(5L),"Manajer Perencanaan & Pengembangan"));
        list.add(new Jabatan(13L, new Jabatan(12L), new Organisasi(46L), new Level(6L),"Supervisor Pengawasan Pekerjaan"));
        list.add(new Jabatan(14L, new Jabatan(13L), new Organisasi(46L), new Level(7L),"Staf Sub Bag. Pengawasan Pekerjaan"));
        list.add(new Jabatan(15L, new Jabatan(12L), new Organisasi(47L), new Level(6L),"Supervisor Pengembangan"));
        list.add(new Jabatan(16L, new Jabatan(15L), new Organisasi(47L), new Level(7L),"Staf Sub Bag. Pengembangan"));
        list.add(new Jabatan(17L, new Jabatan(12L), new Organisasi(48L), new Level(6L),"Supervisor Perencanaan"));
        list.add(new Jabatan(18L, new Jabatan(17L), new Organisasi(48L), new Level(7L),"Staf Sub Bag. Perencanaan"));
        list.add(new Jabatan(19L, new Jabatan(3L), new Organisasi(49L), new Level(5L),"Manajer Produksi & Distribusi 1"));
        list.add(new Jabatan(20L, new Jabatan(19L), new Organisasi(50L), new Level(6L),"Supervisor Pengendalian Distribusi 1"));
        list.add(new Jabatan(21L, new Jabatan(20L), new Organisasi(50L), new Level(7L),"Staf Sub Bag. Pengendalian Distribusi 1"));
        list.add(new Jabatan(22L, new Jabatan(20L), new Organisasi(50L), new Level(7L),"Staf. Sub Bag. Pengendalian Distribusi 1"));
        list.add(new Jabatan(23L, new Jabatan(19L), new Organisasi(51L), new Level(6L),"Supervisor Pengendalian Produksi 1"));
        list.add(new Jabatan(24L, new Jabatan(23L), new Organisasi(51L), new Level(7L),"Staf. Sub Bag. Pengendalian Produksi 1"));
        list.add(new Jabatan(25L, new Jabatan(3L), new Organisasi(52L), new Level(5L),"Manajer Produksi & Distribusi 2"));
        list.add(new Jabatan(26L, new Jabatan(25L), new Organisasi(53L), new Level(6L),"Supervisor Pengendalian Distribusi 2"));
        list.add(new Jabatan(27L, new Jabatan(26L), new Organisasi(53L), new Level(7L),"Staf Sub Bag. Pengendalian Distribusi 2"));
        list.add(new Jabatan(28L, new Jabatan(25L), new Organisasi(54L), new Level(6L),"Supervisor Pengendalian Produksi 2"));
        list.add(new Jabatan(29L, new Jabatan(28L), new Organisasi(54L), new Level(7L),"Staf Sub Bag. Pengendalian Produksi 2"));
        list.add(new Jabatan(30L, new Jabatan(2L), new Organisasi(23L), new Level(4L),"Direktur Umum"));
        list.add(new Jabatan(31L, new Jabatan(30L), new Organisasi(24L), new Level(5L),"Manajer Kesekretariatan"));
        list.add(new Jabatan(32L, new Jabatan(31L), new Organisasi(25L), new Level(6L),"Supervisor Hukum & Perizinan"));
        list.add(new Jabatan(33L, new Jabatan(32L), new Organisasi(25L), new Level(7L),"Staf Sub Bag. Hukum & Perizinan"));
        list.add(new Jabatan(34L, new Jabatan(31L), new Organisasi(26L), new Level(6L),"Supervisor Humas & Protokol"));
        list.add(new Jabatan(35L, new Jabatan(34L), new Organisasi(26L), new Level(7L),"Staf Sub Bag. Humas & Protokol"));
        list.add(new Jabatan(36L, new Jabatan(31L), new Organisasi(27L), new Level(6L),"Supervisor Tata Usaha & Rumah Tangga"));
        list.add(new Jabatan(37L, new Jabatan(36L), new Organisasi(27L), new Level(7L),"Kepala Satpam"));
        list.add(new Jabatan(38L, new Jabatan(36L), new Organisasi(27L), new Level(7L),"Staf Sub Bag. Tata Usaha & Rumah Tangga"));
        list.add(new Jabatan(39L, new Jabatan(30L), new Organisasi(28L), new Level(5L),"Manajer Keuangan"));
        list.add(new Jabatan(40L, new Jabatan(39L), new Organisasi(29L), new Level(6L),"Supervisor Akuntansi"));
        list.add(new Jabatan(41L, new Jabatan(40L), new Organisasi(29L), new Level(7L),"Staf Sub Bag. Akuntansi"));
        list.add(new Jabatan(42L, new Jabatan(39L), new Organisasi(30L), new Level(6L),"Supervisor Anggaran & Pelaporan"));
        list.add(new Jabatan(43L, new Jabatan(42L), new Organisasi(30L), new Level(7L),"Staf Sub Bag. Anggaran & Pelaporan"));
        list.add(new Jabatan(44L, new Jabatan(39L), new Organisasi(31L), new Level(6L),"Supervisor Perbendaharaan"));
        list.add(new Jabatan(45L, new Jabatan(44L), new Organisasi(31L), new Level(7L),"Staf Sub Bag. Perbendaharaan"));
        list.add(new Jabatan(46L, new Jabatan(30L), new Organisasi(32L), new Level(5L),"Manajer Perlengkapan"));
        list.add(new Jabatan(47L, new Jabatan(46L), new Organisasi(33L), new Level(6L),"Supervisor Aset"));
        list.add(new Jabatan(48L, new Jabatan(47L), new Organisasi(33L), new Level(7L),"Staf Sub Bag. Aset"));
        list.add(new Jabatan(49L, new Jabatan(46L), new Organisasi(34L), new Level(6L),"Supervisor Pengadaan"));
        list.add(new Jabatan(50L, new Jabatan(49L), new Organisasi(34L), new Level(7L),"Staf Sub Bag. Pengadaan"));
        list.add(new Jabatan(51L, new Jabatan(46L), new Organisasi(35L), new Level(6L),"Supervisor Persediaan"));
        list.add(new Jabatan(52L, new Jabatan(51L), new Organisasi(35L), new Level(7L),"Staf Sub Bag. Persediaan"));
        list.add(new Jabatan(53L, new Jabatan(30L), new Organisasi(36L), new Level(5L),"Manajer Sumber Daya Manusia & TI"));
        list.add(new Jabatan(54L, new Jabatan(53L), new Organisasi(37L), new Level(6L),"Supervisor Adm. & Pengembangan SDM"));
        list.add(new Jabatan(55L, new Jabatan(54L), new Organisasi(37L), new Level(7L),"Staf Sub Bag. Adm. & Pengembangan SDM"));
        list.add(new Jabatan(56L, new Jabatan(53L), new Organisasi(38L), new Level(6L),"Supervisor Remunerasi & K3"));
        list.add(new Jabatan(57L, new Jabatan(56L), new Organisasi(38L), new Level(7L),"Staf Sub Bag. Remunerasi & K3"));
        list.add(new Jabatan(58L, new Jabatan(53L), new Organisasi(39L), new Level(6L),"Supervisor Teknologi Informasi"));
        list.add(new Jabatan(59L, new Jabatan(58L), new Organisasi(39L), new Level(7L),"Staf Sub Bag. Teknologi Informasi"));
        list.add(new Jabatan(60L, new Jabatan(2L), new Organisasi(3L), new Level(5L),"Manajer Cabang Ajibarang"));
        list.add(new Jabatan(61L, new Jabatan(60L), new Organisasi(4L), new Level(6L),"Supervisor Adm & Keu Cab. Ajibarang"));
        list.add(new Jabatan(62L, new Jabatan(61L), new Organisasi(4L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Ajibarang"));
        list.add(new Jabatan(63L, new Jabatan(60L), new Organisasi(5L), new Level(6L),"Supervisor Pelayanan Cab. Ajibarang"));
        list.add(new Jabatan(64L, new Jabatan(63L), new Organisasi(5L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Ajibarang"));
        list.add(new Jabatan(65L, new Jabatan(60L), new Organisasi(6L), new Level(6L),"Supervisor Teknik Cab. Ajibarang"));
        list.add(new Jabatan(66L, new Jabatan(65L), new Organisasi(6L), new Level(7L),"Staf Sub Bag. Teknik Cab. Ajibarang"));
        list.add(new Jabatan(67L, new Jabatan(2L), new Organisasi(7L), new Level(5L),"Manajer Cabang Banyumas"));
        list.add(new Jabatan(68L, new Jabatan(67L), new Organisasi(8L), new Level(6L),"Supervisor Adm & Keu Cab. Banyumas"));
        list.add(new Jabatan(69L, new Jabatan(68L), new Organisasi(8L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Banyumas"));
        list.add(new Jabatan(70L, new Jabatan(67L), new Organisasi(9L), new Level(6L),"Supervisor Pelayanan Cab. Banyumas"));
        list.add(new Jabatan(71L, new Jabatan(70L), new Organisasi(9L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Banyumas"));
        list.add(new Jabatan(72L, new Jabatan(67L), new Organisasi(10L), new Level(6L),"Supervisor Teknik Cab. Banyumas"));
        list.add(new Jabatan(73L, new Jabatan(72L), new Organisasi(10L), new Level(7L),"Staf Sub Bag. Teknik Cab. Banyumas"));
        list.add(new Jabatan(74L, new Jabatan(2L), new Organisasi(11L), new Level(5L),"Manajer Cabang Purwokerto 1"));
        list.add(new Jabatan(75L, new Jabatan(74L), new Organisasi(12L), new Level(6L),"Supervisor Adm & Keu Cab. Pwkt 1"));
        list.add(new Jabatan(76L, new Jabatan(75L), new Organisasi(12L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Pwkt 1"));
        list.add(new Jabatan(77L, new Jabatan(74L), new Organisasi(13L), new Level(6L),"Supervisor Pelayanan Cab. Pwkt 1"));
        list.add(new Jabatan(78L, new Jabatan(77L), new Organisasi(13L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Pwkt 1"));
        list.add(new Jabatan(79L, new Jabatan(74L), new Organisasi(14L), new Level(6L),"Supervisor Teknik Cab. Pwkt 1"));
        list.add(new Jabatan(80L, new Jabatan(79L), new Organisasi(14L), new Level(7L),"Staf Sub Bag. Teknik Cab. Pwkt 1"));
        list.add(new Jabatan(81L, new Jabatan(2L), new Organisasi(15L), new Level(5L),"Manajer Cabang Purwokerto 2"));
        list.add(new Jabatan(82L, new Jabatan(81L), new Organisasi(16L), new Level(6L),"Supervisor Adm & Keu Cab. Pwkt 2"));
        list.add(new Jabatan(83L, new Jabatan(82L), new Organisasi(16L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Pwkt 2"));
        list.add(new Jabatan(84L, new Jabatan(81L), new Organisasi(17L), new Level(6L),"Supervisor Pelayanan Cab. Pwkt 2"));
        list.add(new Jabatan(85L, new Jabatan(84L), new Organisasi(17L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Pwkt 2"));
        list.add(new Jabatan(86L, new Jabatan(81L), new Organisasi(18L), new Level(6L),"Supervisor Teknik Cab. Pwkt 2"));
        list.add(new Jabatan(87L, new Jabatan(86L), new Organisasi(18L), new Level(7L),"Staf Sub Bag. Teknik Cab. Pwkt 2"));
        list.add(new Jabatan(88L, new Jabatan(2L), new Organisasi(19L), new Level(5L),"Manajer Cabang Wangon"));
        list.add(new Jabatan(89L, new Jabatan(88L), new Organisasi(20L), new Level(6L),"Supervisor Adm & Keu Cab. Wangon"));
        list.add(new Jabatan(90L, new Jabatan(89L), new Organisasi(20L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Wangon"));
        list.add(new Jabatan(91L, new Jabatan(88L), new Organisasi(21L), new Level(6L),"Supervisor Pelayanan Cab. Wangon"));
        list.add(new Jabatan(92L, new Jabatan(91L), new Organisasi(21L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Wangon"));
        list.add(new Jabatan(93L, new Jabatan(88L), new Organisasi(22L), new Level(6L),"Supervisor Teknik Cab. Wangon"));
        list.add(new Jabatan(94L, new Jabatan(93L), new Organisasi(22L), new Level(7L),"Staf Sub Bag. Teknik Cab. Wangon"));
        list.add(new Jabatan(95L, new Jabatan(2L), new Organisasi(55L), new Level(5L),"Manajer Satuan Pengawas Intern"));
        list.add(new Jabatan(96L, new Jabatan(95L), new Organisasi(56L), new Level(6L),"Supervisor Audit Intern"));
        list.add(new Jabatan(97L, new Jabatan(96L), new Organisasi(56L), new Level(7L),"Staf Sub Bag. Audit Intern"));
        list.add(new Jabatan(98L, new Jabatan(95L), new Organisasi(57L), new Level(6L),"Supervisor Standardisasi"));
        list.add(new Jabatan(99L, new Jabatan(98L), new Organisasi(57L), new Level(7L),"Staf Sub Bag. Standardisasi"));
        list.add(new Jabatan(100L, new Jabatan(2L), new Organisasi(58L), new Level(5L),"Manajer Unit Bisnis AMDK"));
        list.add(new Jabatan(101L, new Jabatan(100L), new Organisasi(59L), new Level(6L),"Supervisor Pemasaran"));
        list.add(new Jabatan(102L, new Jabatan(101L), new Organisasi(59L), new Level(7L),"Staf Sub Bag. Pemasaran"));
        list.add(new Jabatan(103L, new Jabatan(100L), new Organisasi(60L), new Level(6L),"Supervisor Produksi"));
        list.add(new Jabatan(104L, new Jabatan(103L), new Organisasi(60L), new Level(7L),"Staf Sub Bag. Produksi"));

//        list.forEach(repository::save);
        repository.saveAll(list);
    }
}
