package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CutiPengajuanServiceImpl implements CutiPengajuanService {
    private final CutiPegawaiRepository repository;
    private final DefConfig defConfig;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;

    @Override
    public Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiPengajuanResponse::from);
    }

    @Override
    public CutiPengajuanResponse findById(Long id) {
        return repository.findById(id).map(CutiPengajuanResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(CutiPengajuanPostRequest request) {
        try {
            int nowYear = LocalDate.now().getYear();
            int startYear = request.getTanggalMulai().getYear();
            int endYear = request.getTanggalSelesai().getYear();

            // CUTI TAHUNAN
            if (request.getJenisCutiId().equals(defConfig.getJenisCutiTahunan())) {
                // pengajuan cuti tahunan untuk tahun depan
                if (startYear > nowYear && endYear > nowYear) {
                    System.out.println("pengajuan cuti tahunan untuk tahun depan");
                    saveCutiService.forNextYear(request);
                }

                // pengajuan cuti menyebrang tahun
                else if (startYear == nowYear && endYear > startYear) {
                    System.out.println("pengajuan cuti menyebrang tahun");
                    saveCutiService.overlappingYear(request);
                }

                // pengajuan cuti antara 1jan sampai 30 juni
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 1, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1jan sampai 30 juni");
                    saveCutiService.between1JanAnd30Jun(request);
                }

                // pengajuan cuti antara 1 juli sampai 31 desember
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 12, 31).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1 juli sampai 31 desember");
                    saveCutiService.between1JulAnd31Dec(request);
                }

                // pengajuan cuti antara tanggal 30 juni sampai 1 juli
                else if (request.getTanggalMulai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1)) &&
                        request.getTanggalSelesai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1))) {
                    System.out.println("pengajuan cuti antara tanggal 30 juni sampai 1 juli");
                    saveCutiService.between30JunAnd1Jul(request);
                }
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
    
    @Override
    public SavedStatus<?> pembatalan(Long id) {
        return null;
    }

    @Override
    public Integer findTotalHariKerja(LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        int totalDays = DateHelper.countWeekdaysBetween(tanggalMulai, tanggalSelesai);
        return totalDays - hariLiburRepository.countByTanggalBetween(tanggalMulai, tanggalSelesai);
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
