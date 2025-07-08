package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CutiPengajuanServiceImpl implements CutiPengajuanService {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository repository;
    private final DefConfig defConfig;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiJenisRepository cutiJenisRepository;
    private final ValidatePengajuanCutiService validatePengajuanCutiService;

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

    /**
     * Menyimpan pengajuan cuti pegawai.
     *
     * @param request pengajuan cuti pegawai yang akan disimpan
     * @return SavedStatus data
     */
    @Override
    public SavedStatus<?> save(CutiPengajuanPostRequest request) {
        try {
            if (redisHelper.validateToken(request.getCsrfToken())) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
            }
            validatePengajuanCutiService.validate(request);

            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));
            CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId())
                    .orElse(null);

            int nowYear = LocalDate.now().getYear();
            int startYear = request.getTanggalMulai().getYear();
            int endYear = request.getTanggalSelesai().getYear();

            int totalDays = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
            int totalHariCuti = totalDays - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());

            CutiPegawai entity = CutiPengajuanPostRequest.toEntity(request, pegawai, jenisCuti, subJenisCuti);
            entity.setJumlahHari(totalDays);
            entity.setJumlahHariKerja(totalHariCuti);

            // CUTI TAHUNAN
            if (request.getJenisCutiId().equals(defConfig.getJenisCutiTahunan())) {
                // pengajuan cuti tahunan untuk tahun depan
                if (startYear > nowYear && endYear > nowYear) {
                    System.out.println("pengajuan cuti tahunan untuk tahun depan");
                    saveCutiService.forNextYear(request, entity);
                }

                // pengajuan cuti menyebrang tahun
                else if (startYear == nowYear && endYear > startYear) {
                    System.out.println("pengajuan cuti menyebrang tahun");
                    saveCutiService.overlappingYear(request, entity);
                }

                // pengajuan cuti antara 1jan sampai 30 juni
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 1, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1jan sampai 30 juni");
                    saveCutiService.between1JanAnd30Jun(request, entity);
                }

                // pengajuan cuti antara 1 juli sampai 31 desember
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 12, 31).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1 juli sampai 31 desember");
                    saveCutiService.between1JulAnd31Dec(request, entity);
                }

                // pengajuan cuti antara tanggal 30 juni sampai 1 juli
                else if (request.getTanggalMulai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1)) &&
                        request.getTanggalSelesai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1))) {
                    System.out.println("pengajuan cuti antara tanggal 30 juni sampai 1 juli");
                    saveCutiService.between30JunAnd1Jul(request, entity);
                }
            } else {
                saveCutiService.saveCutiNonTahunan(request, entity);
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, CutiPengajuanPutRequest request) {
        try {
            if (redisHelper.validateToken(request.getCsrfToken()))
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
            CutiPegawai cutiPegawai = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Cuti Pengajuan"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));
            CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId())
                    .orElse(null);

            int nowYear = LocalDate.now().getYear();
            int startYear = request.getTanggalMulai().getYear();
            int endYear = request.getTanggalSelesai().getYear();

            int totalDays = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
            int totalHariCuti = totalDays - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());

            CutiPegawai entity = CutiPengajuanPutRequest.toEntity(cutiPegawai, request, pegawai, jenisCuti, subJenisCuti);
            entity.setJumlahHari(totalDays);
            entity.setJumlahHariKerja(totalHariCuti);

            // CUTI TAHUNAN
            if (request.getJenisCutiId().equals(defConfig.getJenisCutiTahunan())) {
                // pengajuan cuti tahunan untuk tahun depan
                if (startYear > nowYear && endYear > nowYear) {
                    System.out.println("pengajuan cuti tahunan untuk tahun depan");
                    saveCutiService.forNextYear(request, entity);
                }

                // pengajuan cuti menyebrang tahun
                else if (startYear == nowYear && endYear > startYear) {
                    System.out.println("pengajuan cuti menyebrang tahun");
                    saveCutiService.overlappingYear(request, entity);
                }

                // pengajuan cuti antara 1jan sampai 30 juni
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 1, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1jan sampai 30 juni");
                    saveCutiService.between1JanAnd30Jun(request, entity);
                }

                // pengajuan cuti antara 1 juli sampai 31 desember
                else if (request.getTanggalMulai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1)) &&
                        request.getTanggalSelesai().isBefore(DateHelper.generateDate(startYear, 12, 31).plusDays(1))) {
                    System.out.println("pengajuan cuti antara 1 juli sampai 31 desember");
                    saveCutiService.between1JulAnd31Dec(request, entity);
                }

                // pengajuan cuti antara tanggal 30 juni sampai 1 juli
                else if (request.getTanggalMulai().isBefore(DateHelper.generateDate(startYear, 6, 30).plusDays(1)) &&
                        request.getTanggalSelesai().isAfter(DateHelper.generateDate(startYear, 7, 1).minusDays(1))) {
                    System.out.println("pengajuan cuti antara tanggal 30 juni sampai 1 juli");
                    saveCutiService.between30JunAnd1Jul(request, entity);
                }
            } else {
                saveCutiService.saveCutiNonTahunan(request, entity);
            }

            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil di update");
        } catch (RuntimeException e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> pembatalan(Long id) {
        Optional<CutiPegawai> entity = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING);
        if (entity.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Cuti Pegawai");
        entity.get().setApprovalCutiStatus(EApprovalCutiStatus.CANCELLED);
        repository.save(entity.get());
        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil dibatalkan");
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
