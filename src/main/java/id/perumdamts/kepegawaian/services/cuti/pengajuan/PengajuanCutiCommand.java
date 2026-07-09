package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.helpers.cuti.WorkdayCalculator;
import id.perumdamts.kepegawaian.mapper.cuti.pengajuan.CutiPegawaiMapper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PengajuanCutiCommand {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository repository;
    private final CutiProperties cutiProperties;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiJenisRepository cutiJenisRepository;
    private final CutiApprovalChainGenerator cutiApprovalChainGenerator;
    private final CutiPengajuanValidator cutiPengajuanValidator;

    @Transactional
    public SavedStatus<Long> save(CutiPengajuanPostRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            throw new ConflictException("Duplicate request detected");
        }
        cutiPengajuanValidator.validate(request);

        var pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
        var jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        var subJenisCuti = request.getSubJenisCutiId() != null ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId()) : null;

        int nowYear = LocalDate.now().getYear();
        Set<LocalDate> holidays = hariLiburRepository.findByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai())
                .stream().map(TanggalHariLibur::getTanggal).collect(Collectors.toSet());
        int totalHariCuti = WorkdayCalculator.count(request.getTanggalMulai(), request.getTanggalSelesai(), holidays);

        CutiPegawai entity = CutiPegawaiMapper.toEntity(request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalHariCuti);
        entity.setJumlahHariKerja(totalHariCuti);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            ECutiPeriod period = CutiPeriodClassifier.classify(request.getTanggalMulai(), request.getTanggalSelesai(), nowYear);
            CutiPegawai cutiPegawai = switch (period) {
                case NEXT_YEAR -> saveCutiService.forNextYear(request, entity);
                case OVERLAPPING -> saveCutiService.overlappingYear(request, entity);
                case JAN_JUN -> saveCutiService.between1JanAnd30Jun(request, entity);
                case JUL_DES -> saveCutiService.between1JulAnd31Dec(request, entity);
                case JUN_JUL -> saveCutiService.between30JunAnd1Jul(request, entity);
            };
            cutiApprovalChainGenerator.forPengajuan(cutiPegawai);
        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, CutiPengajuanPutRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            throw new ConflictException("Duplicate request detected");
        }
        var cutiPegawai = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Cuti Pengajuan"));
        var pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
        var jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        var subJenisCuti = request.getSubJenisCutiId() != null ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId()) : null;

        int nowYear = LocalDate.now().getYear();
        Set<LocalDate> holidays = hariLiburRepository.findByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai())
                .stream().map(TanggalHariLibur::getTanggal).collect(Collectors.toSet());
        int totalHariCuti = WorkdayCalculator.count(request.getTanggalMulai(), request.getTanggalSelesai(), holidays);

        CutiPegawai entity = CutiPegawaiMapper.updateEntity(cutiPegawai, request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalHariCuti);
        entity.setJumlahHariKerja(totalHariCuti);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            ECutiPeriod period = CutiPeriodClassifier.classify(request.getTanggalMulai(), request.getTanggalSelesai(), nowYear);
            switch (period) {
                case NEXT_YEAR -> saveCutiService.forNextYear(request, entity);
                case OVERLAPPING -> saveCutiService.overlappingYear(request, entity);
                case JAN_JUN -> saveCutiService.between1JanAnd30Jun(request, entity);
                case JUL_DES -> saveCutiService.between1JulAnd31Dec(request, entity);
                case JUN_JUL -> saveCutiService.between30JunAnd1Jul(request, entity);
            }
        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<String> pembatalan(Long id) {
        var entity = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
        entity.setApprovalCutiStatus(EApprovalCutiStatus.CANCELED);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }
}
