package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SetupOrganisasi implements SetupMaster {
    @Autowired
    private OrganisasiRepository repository;

    @Override
    public void insertBatch() {
        initializeData();
        updateData();
        cleanup();
    }

    private void initializeData() {
        List<Organisasi> list = IntStream.range(1, 76).mapToObj(i -> new Organisasi("organisasi " + i)).toList();
        repository.saveAll(list);
    }

    private void updateData() {
        Organisasi dewas = new Organisasi();
        dewas.setId(1L);
        dewas.setLevelOrg(1);
        dewas.setNama("DEWAN PENGAWAS");
        repository.save(dewas);

        List<Organisasi> list = new ArrayList<>();

        list.add(new Organisasi(2L, "1", new Organisasi(1L), 2, "DIREKTORAT UTAMA", "DIR"));
        list.add(new Organisasi(3L, "1.1", new Organisasi(2L), 3, "DIREKTORAT TEKNIK", "DIR"));
        list.add(new Organisasi(4L, "1.2", new Organisasi(2L), 3, "DIREKTORAT ADMIN & KEUANGAN", "DIR"));
        list.add(new Organisasi(5L, "1.3", new Organisasi(2L), 4, "SATUAN PENGAWAS INTERN", "BPI"));
        list.add(new Organisasi(8L, "1.4", new Organisasi(2L), 4, "CABANG PURWOKERTO 1", "PWK-1"));
        list.add(new Organisasi(9L, "1.5", new Organisasi(2L), 4, "CABANG PURWOKERTO 2", "PWK-2"));
        list.add(new Organisasi(10L, "1.6", new Organisasi(2L), 4, "CABANG AJIBARANG", "AJIB"));
        list.add(new Organisasi(11L, "1.7", new Organisasi(2L), 4, "CABANG WANGON", "WGN"));
        list.add(new Organisasi(12L, "1.8", new Organisasi(2L), 4, "CABANG BANYUMAS", "BMS"));
        list.add(new Organisasi(13L, "1.4.1", new Organisasi(4L), 4, "BAG. KEUANGAN", "KUG"));
        list.add(new Organisasi(15L, "1.4.2", new Organisasi(4L), 4, "BAG. KESEKRETARIATAN", "SEKRE"));
        list.add(new Organisasi(16L, "1.4.3", new Organisasi(4L), 4, "BAG. SUMBER DAYA MANUSIA & TI", "SDM"));
        list.add(new Organisasi(17L, "1.3.1", new Organisasi(3L), 4, "BAG. PERENCANAAN & PENGEMBANGAN", "RENBANG"));
        list.add(new Organisasi(18L, "1.3.2", new Organisasi(3L), 4, "BAG. PRODUKSI & DISTRIBUSI 1", "PRODIS1"));
        list.add(new Organisasi(19L, "1.3.3", new Organisasi(3L), 4, "BAG. PRODUKSI & DISTRIBUSI 2", "PRODIS2"));
        list.add(new Organisasi(20L, "1.3.4", new Organisasi(3L), 4, "BAG. PENGENDALIAN TEKNIK", "DALTEK"));
        list.add(new Organisasi(21L, "1.4.1", new Organisasi(8L), 5, "SUB BAG ADM & KEU CAB. PWKT 1", "PWK-1"));
        list.add(new Organisasi(23L, "1.4.2", new Organisasi(8L), 5, "SUB BAG PELAYANAN CAB. PWKT 1", "PWK-1"));
        list.add(new Organisasi(24L, "1.4.3", new Organisasi(8L), 5, "SUB BAG TEKNIK CAB. PWKT 1", "PWK-1"));
        list.add(new Organisasi(25L, "1.5.1", new Organisasi(9L), 5, "SUB BAG ADM & KEU CAB. PWKT 2", "PWK-2"));
        list.add(new Organisasi(27L, "1.5.2", new Organisasi(9L), 5, "SUB BAG PELAYANAN CAB. PWKT 2", "PWK-2"));
        list.add(new Organisasi(28L, "1.5.3", new Organisasi(9L), 5, "SUB BAG TEKNIK CAB. PWKT 2", "PWK-2"));
        list.add(new Organisasi(29L, "1.6.1", new Organisasi(10L), 5, "SUB BAG ADM & KEU CAB. AJIBARANG", "AJIB"));
        list.add(new Organisasi(31L, "1.6.2", new Organisasi(10L), 5, "SUB BAG PELAYANAN CAB. AJIBARANG", "AJIB"));
        list.add(new Organisasi(32L, "1.6.3", new Organisasi(10L), 5, "SUB BAG TEKNIK CAB. AJIBARANG", "AJIB"));
        list.add(new Organisasi(33L, "1.7.1", new Organisasi(11L), 5, "SUB BAG ADM & KEU CAB. WANGON", "WGN"));
        list.add(new Organisasi(35L, "1.7.2", new Organisasi(11L), 5, "SUB BAG PELAYANAN CAB. WANGON", "WGN"));
        list.add(new Organisasi(36L, "1.7.3", new Organisasi(11L), 5, "SUB BAG TEKNIK CAB. WANGON", "WGN"));
        list.add(new Organisasi(37L, "1.8.1", new Organisasi(12L), 5, "SUB BAG ADM & KEU CAB. BANYUMAS", "BMS"));
        list.add(new Organisasi(39L, "1.8.2", new Organisasi(12L), 5, "SUB BAG PELAYANAN CAB. BANYUMAS", "BMS"));
        list.add(new Organisasi(40L, "1.8.3", new Organisasi(12L), 5, "SUB BAG TEKNIK CAB. BANYUMAS", "BMS"));
        list.add(new Organisasi(41L, "1.4.1.1", new Organisasi(13L), 5, "SUB BAG ANGGARAN & PELAPORAN", "KUG"));
        list.add(new Organisasi(42L, "1.4.1.2", new Organisasi(13L), 5, "SUB BAG AKUNTANSI", "KUG"));
        list.add(new Organisasi(43L, "1.4.1.3", new Organisasi(13L), 5, "SUB BAG PERBENDAHARAAN", "KUG"));
        list.add(new Organisasi(44L, "1.4.2.1", new Organisasi(15L), 5, "SUB BAG HUMAS & PROTOKOL", "SEKRE"));
        list.add(new Organisasi(45L, "1.9.1", new Organisasi(68L), 5, "SUB BAG PEMASARAN", "AMDK"));
        list.add(new Organisasi(46L, "1.4.2.2", new Organisasi(15L), 5, "SUB BAG HUKUM & PERIZINAN", "SEKRE"));
        list.add(new Organisasi(47L, "1.4.2.3", new Organisasi(15L), 5, "SUB BAG TATA USAHA & RUMAH TANGGA", "SEKRE"));
        list.add(new Organisasi(48L, "1.4.4.1", new Organisasi(69L), 5, "SUB BAG ASET", "PRLKP"));
        list.add(new Organisasi(49L, "1.4.4.2", new Organisasi(69L), 5, "SUB BAG PENGADAAN", "PRLKP"));
        list.add(new Organisasi(50L, "1.4.3.1", new Organisasi(16L), 5, "SUB BAG ADM & PENGEMBANGAN SDM", "SDM"));
        list.add(new Organisasi(51L, "1.4.3.2", new Organisasi(16L), 5, "SUB BAG REMUNERASI & K3", "SDM"));
        list.add(new Organisasi(52L, "1.3.1.1", new Organisasi(17L), 5, "SUB BAG PERENCANAAN", "RENBANG"));
        list.add(new Organisasi(53L, "1.3.1.2", new Organisasi(17L), 5, "SUB BAG PENGAWASAN PEKERJAAN", "RENBANG"));
        list.add(new Organisasi(54L, "1.3.2.1", new Organisasi(18L), 5, "SUB BAG PENGENDALIAN PRODUKSI 1", "PRODIS1"));
        list.add(new Organisasi(55L, "1.3.4.1", new Organisasi(20L), 5, "SUB BAG PENJAMINAN MUTU AIR", "DALTEK"));
        list.add(new Organisasi(56L, "1.3.2.2", new Organisasi(18L), 5, "SUB BAG PENGENDALIAN DISTRIBUSI 1", "PRODIS1"));
        list.add(new Organisasi(57L, "1.3.3.1", new Organisasi(19L), 5, "SUB BAG PENGENDALIAN DISTRIBUSI 2", "PRODIS2"));
        list.add(new Organisasi(58L, "1.3.4.2", new Organisasi(20L), 5, "SUB BAG MEKANIKAL & ELEKTRIKAL", "DALTEK"));
        list.add(new Organisasi(60L, "1.3.1", new Organisasi(5L), 5, "SUB BAG STANDARDISASI", "BPI"));
        list.add(new Organisasi(61L, "1.3.2", new Organisasi(5L), 5, "SUB BAG AUDIT INTERN", "BPI"));
        list.add(new Organisasi(63L, "1.3.1.3", new Organisasi(17L), 5, "SUB BAG PENGEMBANGAN 2", "RENBANG"));
        list.add(new Organisasi(64L, "1.3.4.3", new Organisasi(20L), 5, "SUB BAG PENGENDALIAN KEHILANGAN AIR", "DALTEK"));
        list.add(new Organisasi(65L, "1.4.3.3", new Organisasi(16L), 5, "SUB BAG TEKNOLOGI INFORMASI", "SDM"));
        list.add(new Organisasi(68L, "1.9", new Organisasi(2L), 4, "UNIT BISNIS AMDK", "AMDK"));
        list.add(new Organisasi(69L, "1.4.4", new Organisasi(4L), 4, "BAG. PERLENGKAPAN", "PRLKP"));
        list.add(new Organisasi(72L, "1.3.3.2", new Organisasi(19L), 5, "SUB BAG PENGENDALIAN PRODUKSI 2", "PRODIS2"));
        list.add(new Organisasi(74L, "1.4.4.3", new Organisasi(69L), 5, "SUB BAG PERSEDIAAN", "PRLKP"));
        list.add(new Organisasi(75L, "1.9.2", new Organisasi(68L), 5, "SUB BAG PRODUKSI", "AMDK"));

        repository.saveAll(list);
    }

    private void cleanup() {
        Specification<Organisasi> where = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("nama"), "%organisasi%");
        repository.findAll(where).stream().peek(o -> o.setIsDeleted(true))
                .forEach(repository::save);
    }
}
