package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiNominal;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.DetailDasarGajiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailDasarGajiQueryService {
    private final DetailDasarGajiQueryRepository queryRepository;
    private final GolonganRepository golonganRepository;

    public Page<DetailDasarGajiResponse> pageQuery(DetailDasarGajiIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<DetailDasarGajiResponse> listQuery() {
        return queryRepository.listQuery();
    }

    public DetailDasarGajiResponse getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Detail Dasar Gaji not found"));
    }

    public DetailDasarGajiNominal findNominalByGolonganAndMasaKerja(Long golonganId, Integer masaKerja) {
        Golongan golongan = golonganRepository.findById(golonganId)
                .orElseThrow(() -> new NotFoundException("Golongan not found: " + golonganId));
        Integer golonganKode = Integer.parseInt(golongan.getGolongan().split("\\.")[1].replaceAll("\\D.*$", ""));
        return queryRepository.getNominalByGolonganAndMasaKerja(golonganKode, masaKerja)
                .orElseThrow(() -> new NotFoundException("Detail Dasar Gaji not found"));
    }
}
