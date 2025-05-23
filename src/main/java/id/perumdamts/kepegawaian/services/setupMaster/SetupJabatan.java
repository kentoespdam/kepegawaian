package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class SetupJabatan implements SetupMaster {
    @Autowired
    private JabatanRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        initializeData();
        updateData();
        cleanup();
    }

    private void initializeData() {
        List<Jabatan> list = IntStream.range(1, 131).mapToObj(i -> new Jabatan("jabatan " + i)).toList();
        repository.saveAll(list);
    }

    private void updateData() {
        Jabatan dewas = new Jabatan(1L);
        dewas.setKode("0");
        dewas.setOrganisasi(new Organisasi(1L));
        dewas.setLevel(new Level(1L));
        dewas.setNama("Kepala Dewan Pengawas");

        List<Jabatan> list = new ArrayList<>();
        list.add(dewas);
        list.add(new Jabatan(2L,"1", new Jabatan(1L), new Organisasi(2L), new Level(2L),"Direktur Utama"));
        list.add(new Jabatan(3L,"1.1", new Jabatan(2L), new Organisasi(3L), new Level(3L),"Direktur Teknik"));
        list.add(new Jabatan(4L,"1.1.1", new Jabatan(3L), new Organisasi(17L), new Level(5L),"Manajer Perencanaan & Pengembangan"));
        list.add(new Jabatan(5L,"1.1.1.1", new Jabatan(4L), new Organisasi(52L), new Level(6L),"Supervisor Perencanaan"));
        list.add(new Jabatan(6L,"1.1.1.1.1", new Jabatan(5L), new Organisasi(52L), new Level(7L),"Staf Sub Bag. Perencanaan"));
        list.add(new Jabatan(7L,"1.1.1.2", new Jabatan(4L), new Organisasi(53L), new Level(6L),"Supervisor Pengawasan Pekerjaan"));
        list.add(new Jabatan(8L,"1.1.1.2.1", new Jabatan(7L), new Organisasi(53L), new Level(7L),"Staf Sub Bag. Pengawasan Pekerjaan"));
        list.add(new Jabatan(9L,"1.1.2", new Jabatan(3L), new Organisasi(18L), new Level(5L),"Manajer Produksi & Distribusi 1"));
        list.add(new Jabatan(10L,"1.1.2.1", new Jabatan(9L), new Organisasi(54L), new Level(6L),"Supervisor Pengendalian Produksi 1"));
        list.add(new Jabatan(11L,"1.1.2.1.1", new Jabatan(10L), new Organisasi(54L), new Level(7L),"Staf. Sub Bag. Pengendalian Produksi 1"));
        list.add(new Jabatan(12L,"1.1.4.1", new Jabatan(19L), new Organisasi(55L), new Level(6L),"Supervisor Penjaminan Mutu Air"));
        list.add(new Jabatan(13L,"1.1.4.1.1", new Jabatan(12L), new Organisasi(55L), new Level(7L),"Staf Sub Bag. Penjaminan Mutu Air"));
        list.add(new Jabatan(14L,"1.1.3", new Jabatan(3L), new Organisasi(19L), new Level(5L),"Manajer Produksi & Distribusi 2"));
        list.add(new Jabatan(15L,"1.1.2.2", new Jabatan(9L), new Organisasi(56L), new Level(6L),"Supervisor Pengendalian Distribusi 1"));
        list.add(new Jabatan(16L,"1.1.2.2.1", new Jabatan(15L), new Organisasi(56L), new Level(7L),"Staf. Sub Bag. Pengendalian Distribusi 1"));
        list.add(new Jabatan(17L,"1.1.3.1", new Jabatan(14L), new Organisasi(57L), new Level(6L),"Supervisor Pengendalian Distribusi 2"));
        list.add(new Jabatan(18L,"1.1.3.1.1", new Jabatan(17L), new Organisasi(57L), new Level(7L),"Staf Sub Bag. Pengendalian Distribusi 2"));
        list.add(new Jabatan(19L,"1.1.4", new Jabatan(3L), new Organisasi(20L), new Level(5L),"Manajer Pengendalian Teknik"));
        list.add(new Jabatan(20L,"1.1.4.2", new Jabatan(19L), new Organisasi(58L), new Level(6L),"Supervisor Mekanikal & Elektrikal"));
        list.add(new Jabatan(21L,"1.1.4.2.1", new Jabatan(20L), new Organisasi(58L), new Level(7L),"Staf Sub Bag. Mekanikal & Elektrikal"));
        list.add(new Jabatan(22L,"1.1.4.2.1", new Jabatan(20L), new Organisasi(58L), new Level(7L),"Mandor"));
        list.add(new Jabatan(23L,"1.1.4.4", new Jabatan(19L), new Organisasi(59L), new Level(6L),"Ka. Sub Bag. Rehab. Jaringan Pipa"));
        list.add(new Jabatan(24L,"1.1.4.4.1", new Jabatan(23L), new Organisasi(59L), new Level(7L),"Staf Sub Bag. Rehab. Jaringan Pipa"));
        list.add(new Jabatan(25L,"1.2", new Jabatan(2L), new Organisasi(4L), new Level(4L),"Direktur Umum"));
        list.add(new Jabatan(26L,"1.2.1", new Jabatan(25L), new Organisasi(13L), new Level(5L),"Manajer Keuangan"));
        list.add(new Jabatan(27L,"1.2.1.1", new Jabatan(26L), new Organisasi(41L), new Level(6L),"Supervisor Anggaran & Pelaporan"));
        list.add(new Jabatan(28L,"1.2.1.1.1", new Jabatan(27L), new Organisasi(41L), new Level(7L),"Staf Sub Bag. Anggaran & Pelaporan"));
        list.add(new Jabatan(29L,"1.2.1.2", new Jabatan(26L), new Organisasi(42L), new Level(6L),"Supervisor Akuntansi"));
        list.add(new Jabatan(30L,"1.2.1.2.1", new Jabatan(29L), new Organisasi(42L), new Level(7L),"Staf Sub Bag. Akuntansi"));
        list.add(new Jabatan(31L,"1.2.1.3", new Jabatan(26L), new Organisasi(43L), new Level(6L),"Supervisor Perbendaharaan"));
        list.add(new Jabatan(32L,"1.2.1.3.1", new Jabatan(31L), new Organisasi(43L), new Level(7L),"Staf Sub Bag. Perbendaharaan"));
        list.add(new Jabatan(33L,"1.2.1", new Jabatan(25L), new Organisasi(14L), new Level(7L),"Kabag. Humas & Hukum"));
        list.add(new Jabatan(34L,"1.2.2.1", new Jabatan(40L), new Organisasi(44L), new Level(6L),"Supervisor Humas & Protokol"));
        list.add(new Jabatan(35L,"1.2.2.1.1", new Jabatan(34L), new Organisasi(44L), new Level(7L),"Staf Sub Bag. Humas & Protokol"));
        list.add(new Jabatan(36L,"1.2.1.4", new Jabatan(33L), new Organisasi(45L), new Level(6L),"Ka. Sub Bag. Pemasaran"));
        list.add(new Jabatan(37L,"1.8.2.1", new Jabatan(125L), new Organisasi(45L), new Level(7L),"Staf Sub Bag. Pemasaran"));
        list.add(new Jabatan(38L,"1.2.2.2", new Jabatan(40L), new Organisasi(46L), new Level(6L),"Supervisor Hukum & Perizinan"));
        list.add(new Jabatan(39L,"1.2.2.2.1", new Jabatan(38L), new Organisasi(46L), new Level(7L),"Staf Sub Bag. Hukum & Perizinan"));
        list.add(new Jabatan(40L,"1.2.2", new Jabatan(25L), new Organisasi(15L), new Level(5L),"Manajer Kesekretariatan"));
        list.add(new Jabatan(41L,"1.2.2.3", new Jabatan(40L), new Organisasi(47L), new Level(6L),"Supervisor Tata Usaha & Rumah Tangga"));
        list.add(new Jabatan(42L,"1.2.2.3.1", new Jabatan(41L), new Organisasi(47L), new Level(7L),"Staf Sub Bag. Tata Usaha & Rumah Tangga"));
        list.add(new Jabatan(43L,"1.2.2.3.2", new Jabatan(41L), new Organisasi(47L), new Level(7L),"Kepala Satpam"));
        list.add(new Jabatan(44L,"1.2.5.1", new Jabatan(118L), new Organisasi(48L), new Level(6L),"Supervisor Aset"));
        list.add(new Jabatan(45L,"1.2.5.1.1", new Jabatan(44L), new Organisasi(48L), new Level(7L),"Staf Sub Bag. Aset"));
        list.add(new Jabatan(46L,"1.2.5.2", new Jabatan(118L), new Organisasi(49L), new Level(6L),"Supervisor Pengadaan"));
        list.add(new Jabatan(47L,"1.2.5.2.1", new Jabatan(46L), new Organisasi(49L), new Level(7L),"Staf Sub Bag. Pengadaan"));
        list.add(new Jabatan(48L,"1.2.3", new Jabatan(25L), new Organisasi(16L), new Level(5L),"Manajer Sumber Daya Manusia & TI"));
        list.add(new Jabatan(49L,"1.2.3.1", new Jabatan(48L), new Organisasi(50L), new Level(6L),"Supervisor Adm. & Pengembangan SDM"));
        list.add(new Jabatan(50L,"1.2.3.1.1", new Jabatan(49L), new Organisasi(50L), new Level(7L),"Staf Sub Bag. Adm. & Pengembangan SDM"));
        list.add(new Jabatan(51L,"1.2.3.2", new Jabatan(48L), new Organisasi(51L), new Level(6L),"Supervisor Remunerasi & K3"));
        list.add(new Jabatan(52L,"1.2.3.2.1", new Jabatan(51L), new Organisasi(51L), new Level(7L),"Staf Sub Bag. Remunerasi & K3"));
        list.add(new Jabatan(53L,"1.3", new Jabatan(2L), new Organisasi(5L), new Level(5L),"Manajer Satuan Pengawas Intern"));
        list.add(new Jabatan(54L,"1.3.1", new Jabatan(53L), new Organisasi(60L), new Level(6L),"Supervisor Standardisasi"));
        list.add(new Jabatan(55L,"1.3.1.1", new Jabatan(54L), new Organisasi(60L), new Level(7L),"Staf Sub Bag. Standardisasi"));
        list.add(new Jabatan(56L,"1.3.2", new Jabatan(53L), new Organisasi(61L), new Level(6L),"Supervisor Audit Intern"));
        list.add(new Jabatan(57L,"1.3.2.1", new Jabatan(56L), new Organisasi(61L), new Level(7L),"Staf Sub Bag. Audit Intern"));
        list.add(new Jabatan(58L,"1.3.5", new Jabatan(53L), new Organisasi(62L), new Level(5L),"Ka. Sub Bid. Pengendalian Dokumen"));
        list.add(new Jabatan(59L,"1.3.5.1", new Jabatan(58L), new Organisasi(62L), new Level(7L),"Staf Sub Bid. Pengendalian Dokumen"));
        list.add(new Jabatan(60L,"1.2", new Jabatan(2L), new Organisasi(6L), new Level(5L),"Kabid. Penelitian & Pengembangan"));
        list.add(new Jabatan(61L,"1.1.1.3", new Jabatan(4L), new Organisasi(63L), new Level(6L),"Supervisor Pengembangan"));
        list.add(new Jabatan(62L,"1.1.1.3.1", new Jabatan(61L), new Organisasi(63L), new Level(7L),"Staf Sub Bag. Pengembangan"));
        list.add(new Jabatan(63L,"1.1.4.3", new Jabatan(19L), new Organisasi(64L), new Level(6L),"Supervisor Pengendalian Kehilangan Air"));
        list.add(new Jabatan(64L,"1.1.4.3.1", new Jabatan(63L), new Organisasi(64L), new Level(7L),"Staf Sub Bag. Pengendalian Kehilangan Air"));
        list.add(new Jabatan(65L,"1.1", new Jabatan(2L), new Organisasi(7L), new Level(6L),"Kabid. Teknologi Informasi"));
        list.add(new Jabatan(66L,"1.2.3.3", new Jabatan(48L), new Organisasi(65L), new Level(6L),"Supervisor Teknologi Informasi"));
        list.add(new Jabatan(67L,"1.2.3.3.1", new Jabatan(66L), new Organisasi(65L), new Level(7L),"Staf Sub Bag. Teknologi Informasi"));
        list.add(new Jabatan(68L,"1.1.1", new Jabatan(65L), new Organisasi(66L), new Level(5L),"Ka. Sub Bid. Manajemen Informasi"));
        list.add(new Jabatan(69L,"1.1.1.2", new Jabatan(68L), new Organisasi(66L), new Level(7L),"Staf Sub Bid. Manajemen Informasi"));
        list.add(new Jabatan(70L,"1.4", new Jabatan(2L), new Organisasi(8L), new Level(5L),"Manajer Cabang Purwokerto 1"));
        list.add(new Jabatan(71L,"1.4.1", new Jabatan(70L), new Organisasi(21L), new Level(6L),"Supervisor Adm & Keu Cab. Pwkt 1"));
        list.add(new Jabatan(72L,"1.4.1.1", new Jabatan(71L), new Organisasi(21L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Pwkt 1"));
        list.add(new Jabatan(73L,"1.4.2", new Jabatan(70L), new Organisasi(22L), new Level(5L),"Ka. Sub Bag. Umum Cab. Pwkt 1"));
        list.add(new Jabatan(74L,"1.4.2.1", new Jabatan(73L), new Organisasi(22L), new Level(7L),"Staf Sub Bag. Umum Cab. Pwkt 1"));
        list.add(new Jabatan(75L,"1.4.2", new Jabatan(70L), new Organisasi(23L), new Level(6L),"Supervisor Pelayanan Cab. Pwkt 1"));
        list.add(new Jabatan(76L,"1.4.2.2", new Jabatan(75L), new Organisasi(23L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Pwkt 1"));
        list.add(new Jabatan(77L,"1.4.3", new Jabatan(70L), new Organisasi(24L), new Level(6L),"Supervisor Teknik Cab. Pwkt 1"));
        list.add(new Jabatan(78L,"1.4.3.1", new Jabatan(77L), new Organisasi(24L), new Level(7L),"Staf Sub Bag. Teknik Cab. Pwkt 1"));
        list.add(new Jabatan(79L,"1.5", new Jabatan(2L), new Organisasi(9L), new Level(5L),"Manajer Cabang Purwokerto 2"));
        list.add(new Jabatan(80L,"1.5.1", new Jabatan(79L), new Organisasi(25L), new Level(6L),"Supervisor Adm & Keu Cab. Pwkt 2"));
        list.add(new Jabatan(81L,"1.5.1.1", new Jabatan(80L), new Organisasi(25L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Pwkt 2"));
        list.add(new Jabatan(82L,"1.5.1", new Jabatan(79L), new Organisasi(26L), new Level(5L),"Ka. Sub Bag. Umum Cab. Pwkt 2"));
        list.add(new Jabatan(83L,"1.5.1.2", new Jabatan(82L), new Organisasi(26L), new Level(7L),"Staf Sub Bag. Umum Cab. Pwkt 2"));
        list.add(new Jabatan(84L,"1.5.2", new Jabatan(79L), new Organisasi(27L), new Level(6L),"Supervisor Pelayanan Cab. Pwkt 2"));
        list.add(new Jabatan(85L,"1.5.2.1", new Jabatan(84L), new Organisasi(27L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Pwkt 2"));
        list.add(new Jabatan(86L,"1.5.3", new Jabatan(79L), new Organisasi(28L), new Level(6L),"Supervisor Teknik Cab. Pwkt 2"));
        list.add(new Jabatan(87L,"1.5.3.1", new Jabatan(86L), new Organisasi(28L), new Level(7L),"Staf Sub Bag. Teknik Cab. Pwkt 2"));
        list.add(new Jabatan(88L,"1.6", new Jabatan(2L), new Organisasi(10L), new Level(5L),"Manajer Cabang Ajibarang"));
        list.add(new Jabatan(89L,"1.6.1", new Jabatan(88L), new Organisasi(29L), new Level(6L),"Supervisor Adm & Keu Cab. Ajibarang"));
        list.add(new Jabatan(90L,"1.6.1.1", new Jabatan(89L), new Organisasi(29L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Ajibarang"));
        list.add(new Jabatan(91L,"1.6.1", new Jabatan(88L), new Organisasi(30L), new Level(5L),"Ka. Sub Bag. Umum Cab. Ajibarang"));
        list.add(new Jabatan(92L,"1.6.1.2", new Jabatan(91L), new Organisasi(30L), new Level(7L),"Staf Sub Bag. Umum Cab. Ajibarang"));
        list.add(new Jabatan(93L,"1.6.2", new Jabatan(88L), new Organisasi(31L), new Level(6L),"Supervisor Pelayanan Cab. Ajibarang"));
        list.add(new Jabatan(94L,"1.6.2.1", new Jabatan(93L), new Organisasi(31L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Ajibarang"));
        list.add(new Jabatan(95L,"1.6.3", new Jabatan(88L), new Organisasi(32L), new Level(6L),"Supervisor Teknik Cab. Ajibarang"));
        list.add(new Jabatan(96L,"1.6.3.1", new Jabatan(95L), new Organisasi(32L), new Level(7L),"Staf Sub Bag. Teknik Cab. Ajibarang"));
        list.add(new Jabatan(97L,"1.7", new Jabatan(2L), new Organisasi(11L), new Level(5L),"Manajer Cabang Wangon"));
        list.add(new Jabatan(98L,"1.7.1", new Jabatan(97L), new Organisasi(33L), new Level(6L),"Supervisor Adm & Keu Cab. Wangon"));
        list.add(new Jabatan(99L,"1.7.1.1", new Jabatan(98L), new Organisasi(33L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Wangon"));
        list.add(new Jabatan(100L,"1.7.1", new Jabatan(97L), new Organisasi(34L), new Level(5L),"Ka. Sub Bag. Umum Cab. Wangon"));
        list.add(new Jabatan(101L,"1.7.1.2", new Jabatan(100L), new Organisasi(34L), new Level(7L),"Staf Sub Bag. Umum Cab. Wangon"));
        list.add(new Jabatan(102L,"1.7.2", new Jabatan(97L), new Organisasi(35L), new Level(6L),"Supervisor Pelayanan Cab. Wangon"));
        list.add(new Jabatan(103L,"1.7.2.1", new Jabatan(102L), new Organisasi(35L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Wangon"));
        list.add(new Jabatan(104L,"1.7.3", new Jabatan(97L), new Organisasi(36L), new Level(6L),"Supervisor Teknik Cab. Wangon"));
        list.add(new Jabatan(105L,"1.7.3.1", new Jabatan(104L), new Organisasi(36L), new Level(7L),"Staf Sub Bag. Teknik Cab. Wangon"));
        list.add(new Jabatan(106L,"1.8", new Jabatan(2L), new Organisasi(12L), new Level(5L),"Manajer Cabang Banyumas"));
        list.add(new Jabatan(107L,"1.8.1", new Jabatan(106L), new Organisasi(37L), new Level(6L),"Supervisor Adm & Keu Cab. Banyumas"));
        list.add(new Jabatan(108L,"1.8.1.1", new Jabatan(107L), new Organisasi(37L), new Level(7L),"Staf Sub Bag. Adm & Keu Cab. Banyumas"));
        list.add(new Jabatan(109L,"1.8.1", new Jabatan(106L), new Organisasi(38L), new Level(5L),"Ka. Sub Bag. Umum Cab. Banyumas"));
        list.add(new Jabatan(110L,"1.8.1.2", new Jabatan(109L), new Organisasi(38L), new Level(7L),"Staf Sub Bag. Umum Cab. Banyumas"));
        list.add(new Jabatan(111L,"1.8.2", new Jabatan(106L), new Organisasi(39L), new Level(6L),"Supervisor Pelayanan Cab. Banyumas"));
        list.add(new Jabatan(112L,"1.8.2.1", new Jabatan(111L), new Organisasi(39L), new Level(7L),"Staf Sub Bag. Pelayanan Cab. Banyumas"));
        list.add(new Jabatan(113L,"1.8.3", new Jabatan(106L), new Organisasi(40L), new Level(6L),"Supervisor Teknik Cab. Banyumas"));
        list.add(new Jabatan(114L,"1.8.3.1", new Jabatan(113L), new Organisasi(40L), new Level(7L),"Staf Sub Bag. Teknik Cab. Banyumas"));
        list.add(new Jabatan(115L,"1.1.1", new Jabatan(65L), new Organisasi(67L), new Level(6L),"Ka.sub.Bid Keamanan System"));
        list.add(new Jabatan(116L,"1.2.4", new Jabatan(25L), new Organisasi(16L), new Level(5L),"Pjs. Kabag Umum"));
        list.add(new Jabatan(117L,"1.1.1.3", new Jabatan(68L), new Organisasi(66L), new Level(7L),"Admin Pengaduan"));
        list.add(new Jabatan(118L,"1.2.5", new Jabatan(25L), new Organisasi(69L), new Level(5L),"Manajer Perlengkapan"));
        list.add(new Jabatan(119L,"1.8", new Jabatan(2L), new Organisasi(68L), new Level(5L),"Manajer Unit Bisnis AMDK"));
        list.add(new Jabatan(120L,"1.1.2.3", new Jabatan(9L), new Organisasi(71L), new Level(6L),"Supervisor Pengendalian Distribusi 1-2"));
        list.add(new Jabatan(121L,"1.1.3.2", new Jabatan(14L), new Organisasi(72L), new Level(6L),"Supervisor Pengendalian Produksi 2"));
        list.add(new Jabatan(122L,"1.1.3.3", new Jabatan(14L), new Organisasi(73L), new Level(6L),"Supervisor Pengendalian Distribusi 2 baru"));
        list.add(new Jabatan(123L,"1.2.5.3", new Jabatan(118L), new Organisasi(74L), new Level(6L),"Supervisor Persediaan"));
        list.add(new Jabatan(124L,"1.8.1", new Jabatan(119L), new Organisasi(75L), new Level(6L),"Supervisor Produksi"));
        list.add(new Jabatan(125L,"1.8.2", new Jabatan(119L), new Organisasi(45L), new Level(6L),"Supervisor Pemasaran"));
        list.add(new Jabatan(126L,"1.8.1.1", new Jabatan(124L), new Organisasi(75L), new Level(7L),"Staf Sub Bag. Produksi"));
        list.add(new Jabatan(127L,"1.1.2.2.1", new Jabatan(15L), new Organisasi(56L), new Level(7L),"Staf Sub Bag. Pengendalian Distribusi 1"));
        list.add(new Jabatan(128L,"1.1.3.2.1", new Jabatan(121L), new Organisasi(72L), new Level(7L),"Staf Sub Bag. Pengendalian Produksi 2"));
        list.add(new Jabatan(129L,"1.1.3.3.1", new Jabatan(122L), new Organisasi(57L), new Level(7L),"Staf Sub Bag. Pengendalian Distribusi 2"));
        list.add(new Jabatan(130L,"1.2.5.3.1", new Jabatan(123L), new Organisasi(74L), new Level(7L),"Staf Sub Bag. Persediaan"));

        repository.saveAll(list);
    }

    private void cleanup() {
        List<Long> ids = List.of(23L, 24L, 33L, 36L, 58L, 59L, 60L, 65L, 68L, 69L, 73L, 74L, 82L, 83L, 91L, 92L, 100L, 101L, 109L, 110L, 115L, 116L, 117L, 120L, 122L, 129L);
        Specification<Jabatan> where = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("id")).value(ids);
        repository.findAll(where).stream().peek(j -> j.setIsDeleted(true))
                .forEach(repository::save);
    }
}
