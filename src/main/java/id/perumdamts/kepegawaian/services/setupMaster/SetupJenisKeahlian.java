package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.repositories.master.JenisKeahlianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupJenisKeahlian implements SetupMaster {
    @Autowired
    private JenisKeahlianRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<JenisKeahlian> list = new ArrayList<>();
        list.add(new JenisKeahlian(1L, "Pemrograman"));
        list.add(new JenisKeahlian(2L, "Desain Grafis"));
        list.add(new JenisKeahlian(3L, "Bhs. Inggris"));
        list.add(new JenisKeahlian(4L, "Teknisi Komputer"));
        list.add(new JenisKeahlian(5L, "Ahli MAM Muda"));
        list.add(new JenisKeahlian(6L, "Ahli MAM Madya"));
        list.add(new JenisKeahlian(7L, "Ahli MAM Utama"));
        list.add(new JenisKeahlian(8L, "Ahli Akuntansi"));
        list.add(new JenisKeahlian(9L, "Ahli Pengadaan"));
        list.add(new JenisKeahlian(10L, "Assessor"));
        list.add(new JenisKeahlian(11L, "Water Sampling"));
        list.add(new JenisKeahlian(12L, "Manajemen Risiko"));
        list.add(new JenisKeahlian(13L, "Operator PLTD"));
        list.add(new JenisKeahlian(14L, "Perpipaan"));
        list.add(new JenisKeahlian(15L, "SPAM"));
        list.add(new JenisKeahlian(16L, "ASET"));
        repository.saveAll(list);
    }
}
