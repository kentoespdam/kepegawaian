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
            dewas.setLevelOrg(1);
            dewas.setNama("DEWAN PENGAWAS");

            List<Organisasi> list = new ArrayList<>();
            list.add(dewas);
            list.add(new Organisasi(2L, new Organisasi(1L),2,"DIREKTORAT UTAMA"));
            list.add(new Organisasi(3L, new Organisasi(2L),3,"CABANG AJIBARANG"));
            list.add(new Organisasi(4L, new Organisasi(3L),4,"SUB BAG ADM & KEU CAB. AJIBARANG"));
            list.add(new Organisasi(5L, new Organisasi(3L),4,"SUB BAG PELAYANAN CAB. AJIBARANG"));
            list.add(new Organisasi(6L, new Organisasi(3L),4,"SUB BAG TEKNIK CAB. AJIBARANG"));
            list.add(new Organisasi(7L, new Organisasi(2L),3,"CABANG BANYUMAS"));
            list.add(new Organisasi(8L, new Organisasi(7L),4,"SUB BAG ADM & KEU CAB. BANYUMAS"));
            list.add(new Organisasi(9L, new Organisasi(7L),4,"SUB BAG PELAYANAN CAB. BANYUMAS"));
            list.add(new Organisasi(10L, new Organisasi(7L),4,"SUB BAG TEKNIK CAB. BANYUMAS"));
            list.add(new Organisasi(11L, new Organisasi(2L),3,"CABANG PURWOKERTO 1"));
            list.add(new Organisasi(12L, new Organisasi(11L),4,"SUB BAG ADM & KEU CAB. PWKT 1"));
            list.add(new Organisasi(13L, new Organisasi(11L),4,"SUB BAG PELAYANAN CAB. PWKT 1"));
            list.add(new Organisasi(14L, new Organisasi(11L),4,"SUB BAG TEKNIK CAB. PWKT 1"));
            list.add(new Organisasi(15L, new Organisasi(2L),3,"CABANG PURWOKERTO 2"));
            list.add(new Organisasi(16L, new Organisasi(15L),4,"SUB BAG ADM & KEU CAB. PWKT 2"));
            list.add(new Organisasi(17L, new Organisasi(15L),4,"SUB BAG PELAYANAN CAB. PWKT 2"));
            list.add(new Organisasi(18L, new Organisasi(15L),4,"SUB BAG TEKNIK CAB. PWKT 2"));
            list.add(new Organisasi(19L, new Organisasi(2L),3,"CABANG WANGON"));
            list.add(new Organisasi(20L, new Organisasi(19L),4,"SUB BAG ADM & KEU CAB. WANGON"));
            list.add(new Organisasi(21L, new Organisasi(19L),4,"SUB BAG PELAYANAN CAB. WANGON"));
            list.add(new Organisasi(22L, new Organisasi(19L),4,"SUB BAG TEKNIK CAB. WANGON"));
            list.add(new Organisasi(23L, new Organisasi(2L),3,"DIREKTORAT ADMIN & KEUANGAN"));
            list.add(new Organisasi(24L, new Organisasi(23L),4,"BAG. KESEKRETARIATAN"));
            list.add(new Organisasi(25L, new Organisasi(24L),5,"SUB BAG HUKUM & PERIZINAN"));
            list.add(new Organisasi(26L, new Organisasi(24L),5,"SUB BAG HUMAS & PROTOKOL"));
            list.add(new Organisasi(27L, new Organisasi(24L),5,"SUB BAG TATA USAHA & RUMAH TANGGA"));
            list.add(new Organisasi(28L, new Organisasi(23L),4,"BAG. KEUANGAN"));
            list.add(new Organisasi(29L, new Organisasi(28L),5,"SUB BAG AKUNTANSI"));
            list.add(new Organisasi(30L, new Organisasi(28L),5,"SUB BAG ANGGARAN & PELAPORAN"));
            list.add(new Organisasi(31L, new Organisasi(28L),5,"SUB BAG PERBENDAHARAAN"));
            list.add(new Organisasi(32L, new Organisasi(23L),4,"BAG. PERLENGKAPAN"));
            list.add(new Organisasi(33L, new Organisasi(32L),5,"SUB BAG ASET"));
            list.add(new Organisasi(34L, new Organisasi(32L),5,"SUB BAG PENGADAAN"));
            list.add(new Organisasi(35L, new Organisasi(32L),5,"SUB BAG PERSEDIAAN"));
            list.add(new Organisasi(36L, new Organisasi(23L),4,"BAG. SUMBER DAYA MANUSIA & TI"));
            list.add(new Organisasi(37L, new Organisasi(36L),5,"SUB BAG ADM & PENGEMBANGAN SDM"));
            list.add(new Organisasi(38L, new Organisasi(36L),5,"SUB BAG REMUNERASI & K3"));
            list.add(new Organisasi(39L, new Organisasi(36L),5,"SUB BAG TEKNOLOGI INFORMASI"));
            list.add(new Organisasi(40L, new Organisasi(2L),3,"DIREKTORAT TEKNIK"));
            list.add(new Organisasi(41L, new Organisasi(40L),4,"BAG. PENGENDALIAN TEKNIK"));
            list.add(new Organisasi(42L, new Organisasi(41L),5,"SUB BAG MEKANIKAL & ELEKTRIKAL"));
            list.add(new Organisasi(43L, new Organisasi(41L),5,"SUB BAG PENGENDALIAN KEHILANGAN AIR"));
            list.add(new Organisasi(44L, new Organisasi(41L),5,"SUB BAG PENJAMINAN MUTU AIR"));
            list.add(new Organisasi(45L, new Organisasi(40L),4,"BAG. PERENCANAAN & PENGEMBANGAN"));
            list.add(new Organisasi(46L, new Organisasi(45L),5,"SUB BAG PENGAWASAN PEKERJAAN"));
            list.add(new Organisasi(47L, new Organisasi(45L),5,"SUB BAG PENGEMBANGAN 2"));
            list.add(new Organisasi(48L, new Organisasi(45L),5,"SUB BAG PERENCANAAN"));
            list.add(new Organisasi(49L, new Organisasi(40L),4,"BAG. PRODUKSI & DISTRIBUSI 1"));
            list.add(new Organisasi(50L, new Organisasi(49L),5,"SUB BAG PENGENDALIAN DISTRIBUSI 1"));
            list.add(new Organisasi(51L, new Organisasi(49L),5,"SUB BAG PENGENDALIAN PRODUKSI 1"));
            list.add(new Organisasi(52L, new Organisasi(40L),4,"BAG. PRODUKSI & DISTRIBUSI 2"));
            list.add(new Organisasi(53L, new Organisasi(52L),5,"SUB BAG PENGENDALIAN DISTRIBUSI 2"));
            list.add(new Organisasi(54L, new Organisasi(52L),5,"SUB BAG PENGENDALIAN PRODUKSI 2"));
            list.add(new Organisasi(55L, new Organisasi(2L),3,"SATUAN PENGAWAS INTERN"));
            list.add(new Organisasi(56L, new Organisasi(55L),4,"SUB BAG AUDIT INTERN"));
            list.add(new Organisasi(57L, new Organisasi(55L),4,"SUB BAG STANDARDISASI"));
            list.add(new Organisasi(58L, new Organisasi(2L),3,"UNIT BISNIS AMDK"));
            list.add(new Organisasi(59L, new Organisasi(58L),4,"SUB BAG PEMASARAN"));
            list.add(new Organisasi(60L, new Organisasi(58L),4,"SUB BAG PRODUKSI"));

            organisasiRepository.saveAll(list);
//            list.forEach(organisasiRepository::save);
        } catch (Exception e) {
            log.error("Failed to setup Organisasi", e);
        }
    }
}
