package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKeahlianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenisKeahlian implements SetupMaster {
    private final JenisKeahlianRepository repository;

    @Override
    public void insertBatch() {
        List<JenisKeahlian> list = new ArrayList<>();
        list.add(new JenisKeahlian("Pemrograman"));
        list.add(new JenisKeahlian("Desain Grafis"));
        list.add(new JenisKeahlian("Bhs. Inggris"));
        list.add(new JenisKeahlian("Teknisi Komputer"));
        list.add(new JenisKeahlian("Ahli MAM Muda"));
        list.add(new JenisKeahlian("Ahli MAM Madya"));
        list.add(new JenisKeahlian("Ahli MAM Utama"));
        list.add(new JenisKeahlian("Ahli Akuntansi"));
        list.add(new JenisKeahlian("Ahli Pengadaan"));
        list.add(new JenisKeahlian("Assessor"));
        list.add(new JenisKeahlian("Water Sampling"));
        list.add(new JenisKeahlian("Manajemen Risiko"));
        list.add(new JenisKeahlian("Operator PLTD"));
        list.add(new JenisKeahlian("Perpipaan"));
        list.add(new JenisKeahlian("SPAM"));
        list.add(new JenisKeahlian("ASET"));
        repository.saveAll(list);
    }
}
