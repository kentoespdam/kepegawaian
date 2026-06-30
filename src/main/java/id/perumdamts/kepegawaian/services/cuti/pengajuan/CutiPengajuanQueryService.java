package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiPengajuanQueryRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CutiPengajuanQueryService {
    private final CutiPengajuanQueryRepository queryRepository;
    private final HariLiburRepository hariLiburRepository;

    public Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request) {
        return queryRepository.pageQuery(request);
    }

    public CutiPengajuanResponse findById(Long id) {
        return queryRepository.getById(id);
    }

    public Integer findTotalHariKerja(LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        int totalDays = DateHelper.countWeekdaysBetween(tanggalMulai, tanggalSelesai);
        return totalDays - hariLiburRepository.countByTanggalBetween(tanggalMulai, tanggalSelesai);
    }
}
