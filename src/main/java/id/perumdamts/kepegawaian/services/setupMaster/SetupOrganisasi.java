package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetupOrganisasi implements SetupMaster {
    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Override
    public void insertBatch() {
        try {
            Organisasi dewas = new Organisasi();
            dewas.setId(1L);
            dewas.setKode("0");
            dewas.setLevelOrg(1);
            dewas.setNama("DEWAN PENGAWAS");

            List<Organisasi> list = new ArrayList<>();
            list.add(dewas);
            list.add(new Organisasi(2L, "1", new Organisasi(1L),2, "DIREKTORAT UTAMA"));
            list.add(new Organisasi(3L, "1.1", new Organisasi(2L),3, "DIREKTORAT ADMIN & KEUANGAN"));
            list.add(new Organisasi(4L, "1.2", new Organisasi(2L),3, "DIREKTORAT TEKNIK"));
            list.add(new Organisasi(5L, "1.3", new Organisasi(2L),4, "SATUAN PENGAWAS INTERN"));
            list.add(new Organisasi(6L, "1.3.1", new Organisasi(5L),5, "SUB BAG AUDIT INTERN"));
            list.add(new Organisasi(7L, "1.3.2", new Organisasi(5L),5, "SUB BAG STANDARDISASI"));
            list.add(new Organisasi(8L, "1.4", new Organisasi(2L),4, "CABANG PURWOKERTO 1"));
            list.add(new Organisasi(9L, "1.4.1", new Organisasi(8L),5, "SUB BAG ADM & KEU CAB. PWKT 1"));
            list.add(new Organisasi(10L, "1.4.2", new Organisasi(8L),5, "SUB BAG PELAYANAN CAB. PWKT 1"));
            list.add(new Organisasi(11L, "1.4.3", new Organisasi(8L),5, "SUB BAG TEKNIK CAB. PWKT 1"));
            list.add(new Organisasi(12L, "1.5", new Organisasi(2L),4, "CABANG PURWOKERTO 2"));
            list.add(new Organisasi(13L, "1.5.1", new Organisasi(12L),5, "SUB BAG ADM & KEU CAB. PWKT 2"));
            list.add(new Organisasi(14L, "1.5.2", new Organisasi(12L),5, "SUB BAG PELAYANAN CAB. PWKT 2"));
            list.add(new Organisasi(15L, "1.5.3", new Organisasi(12L),5, "SUB BAG TEKNIK CAB. PWKT 2"));
            list.add(new Organisasi(16L, "1.6", new Organisasi(2L),4, "CABANG BANYUMAS"));
            list.add(new Organisasi(17L, "1.6.1", new Organisasi(16L),5, "SUB BAG ADM & KEU CAB. BANYUMAS"));
            list.add(new Organisasi(18L, "1.6.2", new Organisasi(16L),5, "SUB BAG PELAYANAN CAB. BANYUMAS"));
            list.add(new Organisasi(19L, "1.6.3", new Organisasi(16L),5, "SUB BAG TEKNIK CAB. BANYUMAS"));
            list.add(new Organisasi(20L, "1.7", new Organisasi(2L),4, "CABANG WANGON"));
            list.add(new Organisasi(21L, "1.7.1", new Organisasi(20L),5, "SUB BAG ADM & KEU CAB. WANGON"));
            list.add(new Organisasi(22L, "1.7.2", new Organisasi(20L),5, "SUB BAG PELAYANAN CAB. WANGON"));
            list.add(new Organisasi(23L, "1.7.3", new Organisasi(20L),5, "SUB BAG TEKNIK CAB. WANGON"));
            list.add(new Organisasi(24L, "1.8", new Organisasi(2L),4, "CABANG AJIBARANG"));
            list.add(new Organisasi(25L, "1.8.1", new Organisasi(24L),5, "SUB BAG ADM & KEU CAB. AJIBARANG"));
            list.add(new Organisasi(26L, "1.8.2", new Organisasi(24L),5, "SUB BAG PELAYANAN CAB. AJIBARANG"));
            list.add(new Organisasi(27L, "1.8.3", new Organisasi(24L),5, "SUB BAG TEKNIK CAB. AJIBARANG"));
            list.add(new Organisasi(28L, "1.9", new Organisasi(2L),4, "UNIT BISNIS AMDK"));
            list.add(new Organisasi(29L, "1.9.1", new Organisasi(28L),5, "SUB BAG PRODUKSI"));
            list.add(new Organisasi(30L, "1.9.2", new Organisasi(28L),5, "SUB BAG PEMASARAN"));
            list.add(new Organisasi(31L, "1.2.1", new Organisasi(4L),4, "PERENCANAAN DAN PENGEMBANGAN"));
            list.add(new Organisasi(32L, "1.2.1.1", new Organisasi(31L),5, "SUB BAG PERENCANAAN"));
            list.add(new Organisasi(33L, "1.2.1.2", new Organisasi(31L),5, "SUB BAG PENGEMBANGAN"));
            list.add(new Organisasi(34L, "1.2.1.3", new Organisasi(31L),5, "SUB BAG PENGAWASAN PEKERJAAN"));
            list.add(new Organisasi(35L, "1.2.2", new Organisasi(4L),4, "BAG. PRODUKSI & DISTRIBUSI 1"));
            list.add(new Organisasi(36L, "1.2.2.1", new Organisasi(35L),5, "SUB BAG PENGENDALIAN PRODUKSI 1"));
            list.add(new Organisasi(37L, "1.2.2.2", new Organisasi(35L),5, "SUB BAG PENGENDALIAN DISTRIBUSI 1"));
            list.add(new Organisasi(38L, "1.2.3", new Organisasi(4L),4, "BAG. PRODUKSI & DISTRIBUSI 2"));
            list.add(new Organisasi(39L, "1.2.3.1", new Organisasi(38L),5, "SUB BAG PENGENDALIAN PRODUKSI 2"));
            list.add(new Organisasi(40L, "1.2.3.2", new Organisasi(38L),5, "SUB BAG PENGENDALIAN DISTRIBUSI 2"));
            list.add(new Organisasi(41L, "1.2.4", new Organisasi(4L),4, "BAG. PENGENDALIAN TEKNIK"));
            list.add(new Organisasi(42L, "1.2.4.1", new Organisasi(41L),5, "SUB BAG MEKANIKAL & ELEKTRIKAL"));
            list.add(new Organisasi(43L, "1.2.4.2", new Organisasi(41L),5, "SUB BAG PENGENDALIAN KEHILANGAN AIR"));
            list.add(new Organisasi(44L, "1.2.4.3", new Organisasi(41L),5, "SUB BAG PENJAMINAN MUTU AIR"));
            list.add(new Organisasi(45L, "1.1.1", new Organisasi(3L),4, "BAG. KESEKRETARIATAN"));
            list.add(new Organisasi(46L, "1.1.1.1", new Organisasi(45L),5, "SUB BAG HUKUM & PERIZINAN"));
            list.add(new Organisasi(47L, "1.1.1.2", new Organisasi(45L),5, "SUB BAG HUMAS & PROTOKOL"));
            list.add(new Organisasi(48L, "1.1.1.3", new Organisasi(45L),5, "SUB BAG TATA USAHA & RUMAH TANGGA"));
            list.add(new Organisasi(49L, "1.1.2", new Organisasi(3L),4, "BAG. KEUANGAN"));
            list.add(new Organisasi(50L, "1.1.2.1", new Organisasi(49L),5, "SUB BAG AKUNTANSI"));
            list.add(new Organisasi(51L, "1.1.2.2", new Organisasi(49L),5, "SUB BAG ANGGARAN & PELAPORAN"));
            list.add(new Organisasi(52L, "1.1.2.3", new Organisasi(49L),5, "SUB BAG PERBENDAHARAAN"));
            list.add(new Organisasi(53L, "1.1.3", new Organisasi(3L),4, "BAG. SUMBER DAYA MANUSIA & TI"));
            list.add(new Organisasi(54L, "1.1.3.1", new Organisasi(53L),5, "SUB BAG ADM & PENGEMBANGAN SDM"));
            list.add(new Organisasi(55L, "1.1.3.2", new Organisasi(53L),5, "SUB BAG REMUNERASI & K3"));
            list.add(new Organisasi(56L, "1.1.3.3", new Organisasi(53L),5, "SUB BAG TEKNOLOGI INFORMASI"));
            list.add(new Organisasi(57L, "1.1.4", new Organisasi(3L),4, "BAG. PERLENGKAPAN"));
            list.add(new Organisasi(58L, "1.1.4.1", new Organisasi(57L),5, "SUB BAG ASET"));
            list.add(new Organisasi(59L, "1.1.4.2", new Organisasi(57L),5, "SUB BAG PENGADAAN"));
            list.add(new Organisasi(60L, "1.1.4.3", new Organisasi(57L),5, "SUB BAG PERSEDIAAN"));

//            organisasiRepository.saveAll(list);
            list.forEach(organisasiRepository::save);
        } catch (Exception e) {
            log.error("Failed to setup Organisasi", e);
        }
    }
}
