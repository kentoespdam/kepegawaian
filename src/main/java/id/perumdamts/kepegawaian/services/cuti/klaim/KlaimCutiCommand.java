package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKlaimDetailRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainService;
import id.perumdamts.kepegawaian.services.cuti.klaim.CutiKlaimValidator;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KlaimCutiCommand {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiKlaimValidator cutiKlaimValidator;
    private final HariLiburRepository hariLiburRepository;
    private final CutiKlaimDetailRepository cutiKlaimDetailRepository;
    private final CutiApprovalChainRepository cutiApprovalChainRepository;
    private final CutiApprovalRepository cutiApprovalRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final CutiApprovalChainService cutiApprovalChainService;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;

    @Transactional
    public SavedStatus<?> save(CutiPengajuanKlaimPostRequest request) {
        CutiPegawai validCutiPegawai = cutiKlaimValidator.validateKlaim(request);
        CutiPegawai entity = CutiPengajuanKlaimPostRequest.toEntity(validCutiPegawai, request);

        Jabatan spvSDM = new Jabatan(supervisorSdm);
        entity.setPicSaatIni(spvSDM);

        List<LocalDate> tanggalLiburList = hariLiburRepository
                .findByTanggalBetween(request.getListHari().getFirst(), request.getListHari().getLast())
                .stream().map(TanggalHariLibur::getTanggal).toList();

        List<LocalDate> tanggalKlaimList = DateHelper.getWorkingDays(request.getListHari(), tanggalLiburList);
        int totalHariCuti = tanggalKlaimList.size();

        int totalRemainingQuota = validCutiPegawai.getRiwayatKuota0() + validCutiPegawai.getRiwayatKuota1();

        if (totalHariCuti < 1 || totalHariCuti > entity.getRefCuti().getJumlahHariKerja()) {
            throw new RuntimeException("Klaim Cuti minimal 1 hari, maksimal " + entity.getRefCuti().getJumlahHariKerja() + "hari");
        }

        entity.setTanggalMulai(tanggalKlaimList.getFirst());
        entity.setTanggalSelesai(tanggalKlaimList.getLast());
        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
        entity.setJumlahHari(totalHariCuti);
        entity.setJumlahHariKerja(totalHariCuti);

        CutiPegawai save = cutiPegawaiRepository.save(entity);
        List<CutiKlaimDetail> cutiKlaimDetailList = tanggalKlaimList.stream()
                .map(tanggal -> new CutiKlaimDetail(save, tanggal))
                .toList();
        cutiKlaimDetailRepository.saveAll(cutiKlaimDetailList);
        cutiApprovalChainService.generateApprovalKlaimChain(save);

        return SavedStatus.build(ESaveStatus.SUCCESS, "Pengajuan Klaim Cuti Berhasil disimpan");
    }

    @Transactional
    public SavedStatus<?> update(Long id, CutiPengajuanKlaimPostRequest request) {
        CutiPegawai cutiPegawai = cutiPegawaiRepository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

        List<LocalDate> tanggalLiburList = hariLiburRepository
                .findByTanggalBetween(request.getListHari().getFirst(), request.getListHari().getLast())
                .stream().map(TanggalHariLibur::getTanggal).toList();

        List<LocalDate> tanggalKlaimList = DateHelper.getWorkingDays(request.getListHari(), tanggalLiburList);
        int totalHariCuti = tanggalKlaimList.size();

        int totalRemainingQuota = cutiPegawai.getRefCuti().getRiwayatKuota0() + cutiPegawai.getRefCuti().getRiwayatKuota1();

        MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        if (totalRemainingQuota < totalHariCuti) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        cutiPegawai.setAlasan(request.getKeterangan());
        cutiPegawai.setTanggalMulai(tanggalKlaimList.getFirst());
        cutiPegawai.setTanggalSelesai(tanggalKlaimList.getLast());
        cutiPegawai.setKuotaAwal(totalRemainingQuota);
        cutiPegawai.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
        cutiPegawai.setJumlahHari(totalHariCuti);
        cutiPegawai.setJumlahHariKerja(totalHariCuti);

        CutiPegawai save = cutiPegawaiRepository.save(cutiPegawai);

        List<CutiKlaimDetail> klaimDetails = cutiKlaimDetailRepository.findByRefCuti_id(id);
        cutiKlaimDetailRepository.deleteAll(klaimDetails);

        List<CutiKlaimDetail> cutiKlaimDetailList = tanggalKlaimList.stream()
                .map(tanggal -> new CutiKlaimDetail(save, tanggal))
                .toList();
        cutiKlaimDetailRepository.saveAll(cutiKlaimDetailList);

        return SavedStatus.build(ESaveStatus.SUCCESS, "Pengajuan Klaim Cuti Berhasil diupdate");
    }

    @Transactional
    public SavedStatus<?> saveKlaim(CutiApprovalPostRequest request) {
        if (redisHelper.validateToken(request.getCsrfToken())) {
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }

        CutiPegawai cutiPegawai = cutiPegawaiRepository.findById(request.getCutiId())
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
        Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver Pegawai not found"));

        // PRESERVED: see kepegawaian-s5n
        if (!cutiPegawai.getPicSaatIni().equals(approver.getJabatan())) {
            throw new RuntimeException("Approver Pegawai not found");
        }

        CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawai, approver);

        cutiPegawai.setApprovalCutiStatus(request.getApprovalStatus());
        if (!request.getApprovalStatus().equals(EApprovalCutiStatus.APPROVED)) {
            cutiPegawaiRepository.save(cutiPegawai);
        } else {
            int nowYear = LocalDate.now().getYear();
            LocalDate tanggalMulai = cutiPegawai.getTanggalMulai();
            LocalDate tanggalSelesai = cutiPegawai.getTanggalSelesai();
            int startYear = tanggalMulai.getYear();
            int endYear = tanggalSelesai.getYear();

            if (startYear > nowYear && endYear > nowYear) {
                forNextYear(cutiPegawai, entity);
            } else if (startYear == nowYear && endYear > startYear) {
                overlappingYear(cutiPegawai, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                between1JanAnd30Jun(cutiPegawai, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                between1JulAnd31Dec(cutiPegawai, entity);
            } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                between30JunAnd1Jul(cutiPegawai, entity);
            } else {
                throw new RuntimeException("Invalid request");
            }
        }
        cutiApprovalChainRepository.findByRefCutiIdAndJabatanId(cutiPegawai.getId(), approver.getJabatan().getId())
                .ifPresent(chain -> {
                    chain.setReadWriteStatus(EReadWriteStatus.READ);
                    chain.setApprovalStatus(request.getApprovalStatus());
                    cutiApprovalChainRepository.save(chain);
                });

        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disetujui");
    }

    private void forNextYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        // PRESERVED: see kepegawaian-ciw
        int currentYear = cutiPegawai.getTanggalMulai().getYear() - 1;
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();
        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        cutiApprovalRepository.save(cutiApproval);
    }

    private void overlappingYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();

        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        cutiApprovalRepository.save(cutiApproval);
    }

    private void between1JanAnd30Jun(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        // PRESERVED: see kepegawaian-sfq
        LocalDate now = LocalDate.now();
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int totalLeaveDays = cutiPegawai.getJumlahHariKerja();
        CutiPegawai referenceCuti = cutiPegawai.getRefCuti();
        int previousYearUsedQuota = referenceCuti.getRiwayatPakai0();
        int currentYearUsedQuota = referenceCuti.getRiwayatPakai1();
        int nextYearQuota = referenceCuti.getRiwayatKuota1();

        if (now.isAfter(LocalDate.of(currentYear, 6, 30))
                && totalLeaveDays < previousYearUsedQuota) {
            cutiApproval.setApprovalStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
            throw new RuntimeException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
        }

        int remainingCurrentYearQuota = totalLeaveDays - previousYearUsedQuota;
        cutiPegawai.setRiwayatKuota0(referenceCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(previousYearUsedQuota);
        cutiPegawai.setRiwayatSisa0(referenceCuti.getRiwayatSisa0());
        cutiPegawai.setRiwayatKuota1(nextYearQuota);
        cutiPegawai.setRiwayatPakai1(currentYearUsedQuota + remainingCurrentYearQuota);
        cutiPegawai.setRiwayatSisa1(nextYearQuota - (currentYearUsedQuota + remainingCurrentYearQuota));
        cutiPegawai.getRefCuti().setIsClaimed(true);

        cutiKuotaRepository.findByPegawai_IdAndTahun(cutiPegawai.getPegawai().getId(), currentYear).ifPresent(cutiKuota -> {
            cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - currentYearUsedQuota + remainingCurrentYearQuota);
            cutiKuota.setSisaKuota(nextYearQuota - (currentYearUsedQuota + remainingCurrentYearQuota));
            cutiKuotaRepository.save(cutiKuota);
        });

        cutiPegawaiRepository.save(cutiPegawai);
        cutiApprovalRepository.save(cutiApproval);
    }

    private void between1JulAnd31Dec(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        int jumlahHariKlaim = cutiPegawai.getJumlahHariKerja();
        int jumlahHariPengajuan = refCuti.getJumlahHariKerja();
        int riwayatPakai0 = refCuti.getRiwayatPakai0();

        if (jumlahHariKlaim == jumlahHariPengajuan) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(riwayatPakai0);
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawai.getRefCuti().setIsClaimed(true);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
            return;
        }

        int sisa = jumlahHariPengajuan - jumlahHariKlaim;
        cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(jumlahHariKlaim);
        cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0() + sisa);
        cutiPegawai.getRefCuti().setIsClaimed(true);
        updateKuotaCuti(cutiPegawai, cutiApproval, sisa);
    }

    private void between30JunAnd1Jul(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        int jumlahHariKlaim = cutiPegawai.getJumlahHariKerja();
        int jumlahHariPengajuan = refCuti.getJumlahHariKerja();
        int riwayatPakai0 = refCuti.getRiwayatPakai0();

        if (jumlahHariKlaim < riwayatPakai0) {
            cutiApproval.setApprovalStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
            throw new RuntimeException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
        }

        cutiPegawai.getRefCuti().setIsClaimed(true);
        if (jumlahHariKlaim == jumlahHariPengajuan) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(riwayatPakai0);
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
            return;
        }

        int sisa = jumlahHariPengajuan - jumlahHariKlaim;
        cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(jumlahHariKlaim);
        cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0() + sisa);
        updateKuotaCuti(cutiPegawai, cutiApproval, sisa);
    }

    private void updateKuotaCuti(CutiPegawai cutiPegawai, CutiApproval cutiApproval, int sisa) {
        cutiKuotaRepository.findByPegawai_IdAndTahun(cutiPegawai.getPegawai().getId(), cutiPegawai.getTanggalMulai().getYear())
                .ifPresent(kuota -> {
                    kuota.setKuotaTerpakai(kuota.getKuotaTerpakai() - sisa);
                    kuota.setSisaKuota(kuota.getSisaKuota() + sisa);
                    cutiKuotaRepository.save(kuota);
                });
        cutiPegawaiRepository.save(cutiPegawai);
        cutiApprovalRepository.save(cutiApproval);
    }

    private void separateCutiWithNextYear(CutiPegawai cutiPegawai, int currentYear, int nextYear, Long pegawaiId) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        cutiPegawai.getRefCuti().setIsClaimed(true);
        if (cutiPegawai.getJumlahHariKerja().equals(cutiPegawai.getRefCuti().getJumlahHariKerja())) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(refCuti.getRiwayatPakai0());
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawai.setRiwayatKuota1(refCuti.getRiwayatKuota1());
            cutiPegawai.setRiwayatPakai1(refCuti.getRiwayatPakai1());
            cutiPegawai.setRiwayatSisa1(refCuti.getRiwayatSisa1());
            cutiPegawaiRepository.save(cutiPegawai);
            return;
        }

        Integer currentYearRemaining = refCuti.getRiwayatKuota0();
        Integer nextYearRemaining = refCuti.getRiwayatKuota1();
        Integer riwayatPakai0 = refCuti.getRiwayatPakai0();
        Integer riwayatPakai1 = refCuti.getRiwayatPakai1();
        Integer totalDays = cutiPegawai.getJumlahHariKerja();

        int remainingAfterCurrentYear = totalDays - currentYearRemaining;
        if (remainingAfterCurrentYear > 0) {
            if (nextYearRemaining < remainingAfterCurrentYear) {
                throw new RuntimeException("Kuota Cuti Tahun depan tidak tersedia! sisa kuota: " + nextYearRemaining + " hari");
            }

            cutiPegawai.setRiwayatKuota0(currentYearRemaining);
            cutiPegawai.setRiwayatPakai0(currentYearRemaining);
            cutiPegawai.setRiwayatSisa0(0);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, currentYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai0 + currentYearRemaining);
                cutiKuota.setSisaKuota(0);
                cutiKuotaRepository.save(cutiKuota);
            });

            cutiPegawai.setRiwayatKuota1(nextYearRemaining);
            cutiPegawai.setRiwayatPakai1(remainingAfterCurrentYear);
            cutiPegawai.setRiwayatSisa1(nextYearRemaining - remainingAfterCurrentYear);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, nextYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai1 + remainingAfterCurrentYear);
                cutiKuota.setSisaKuota(nextYearRemaining - remainingAfterCurrentYear);
                cutiKuotaRepository.save(cutiKuota);
            });
        } else {
            cutiPegawai.setRiwayatKuota0(currentYearRemaining);
            cutiPegawai.setRiwayatPakai0(totalDays);
            cutiPegawai.setRiwayatSisa0(currentYearRemaining - totalDays);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, currentYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai0 + totalDays);
                cutiKuota.setSisaKuota(currentYearRemaining - totalDays);
                cutiKuotaRepository.save(cutiKuota);
            });
        }

        cutiPegawaiRepository.save(cutiPegawai);
    }
}
