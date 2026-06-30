package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaAllocationResult;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaAllocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaveCutiService {
    private final CutiPegawaiRepository repository;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final HariLiburRepository hariLiburRepository;
    private final JabatanRepository jabatanRepository;
    private final CutiProperties cutiProperties;

    /**
     * Pengajuan cuti untuk tahun depan.
     * Pengajuan diasumsikan pada tahun depan, maka:
     * 1. Jika ada, ambil jatah cuti tahun berjalan, atau
     * 2. Jika ada, ambil jatah cuti tahun depan, atau
     * 3. Jika jumlah cuti tahun berjalan dan/atau jatah cuti tahun depan tidak ada maka batalkan.
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and subtype of leave.
     * @param entity  the entity containing the leave information.
     * @return CutiPegawai
     * @throws RuntimeException if the employee or leave type is unknown, or if leave quotas
     *                          are insufficient for the requested leave period.
     */
    public CutiPegawai forNextYear(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalDays = entity.getJumlahHariKerja();

        int currentYear = request.getTanggalMulai().getYear() - 1;
        int nextYear = request.getTanggalSelesai().getYear();
        this.separateCutiWithNextYear(entity, currentYear, nextYear, request.getPegawaiId(), totalDays);
        setPic(entity);
        return repository.save(entity);
    }

    /**
     * Pengajuan Cuti menyebrang tahun (sebagian tahun ini, sebagian tahun depan)
     * 1. pengajuan diasumsikan pada akhir tahun berjalan sampai awal tahun depan
     * 2. jika ada, ambil jatah cuti tahun berjalan, atau
     * 3. jika ada, ambil jatah cuti tahun depan, atau
     * 4. jika jumlah cuti tahun berjalan dan/atau jatah cuti tahun depan tidak ada maka batalkan
     * 5. cuti tahun berjalan harus ambil dari jatah cuti tahun berjalan
     *
     * @param request the leave request containing employee ID, leave type, start and end dates, and subtype of leave.
     * @param entity  the entity containing the leave information.
     * @return CutiPegawai
     * @throws RuntimeException if the employee or leave type is unknown, or if leave quotas are insufficient.
     */
    public CutiPegawai overlappingYear(CutiPengajuanPostRequest request, CutiPegawai entity) {
        // total cuti yang diambil
        int totalDays = entity.getJumlahHariKerja();

        // ambil jatah cuti tahun berjalan dan tahun depan
        int currentYear = request.getTanggalMulai().getYear();
        int nextYear = request.getTanggalSelesai().getYear();

        this.separateCutiWithNextYear(entity, currentYear, nextYear, request.getPegawaiId(), totalDays);
        setPic(entity);
        return repository.save(entity);
    }

    /**
     * Pengajuan cuti antara 1 januari s/d 30 juni maka cek kuota tahun ini + sisa tahun lalu jika ada
     * 1. pengajuan diasumsikan pada tanggal 1 januari sampai 30 juni
     * 2. jika ada, ambil jatah cuti tahun lalu, atau
     * 3. jika ada, ambil jatah cuti tahun berjalan, atau
     * 4. jika jumlah cuti tahun lalu dan/atau jatah cuti tahun berjalan tidak ada maka batalkan
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @param entity  the entity containing the leave information.
     * @return CutiPegawai
     * @throws RuntimeException if the employee or leave type is unknown, or if leave quotas
     *                          are insufficient for the requested leave period.
     */
    public CutiPegawai between1JanAnd30Jun(CutiPengajuanPostRequest request, CutiPegawai entity) {
        // total cuti yang diambil
        int totalDays = entity.getJumlahHariKerja();

        // ambil tahun
        int prevYear = request.getTanggalMulai().getYear() - 1;
        int currentYear = request.getTanggalMulai().getYear();

        this.separateCutiWithPreviousYear(entity, prevYear, currentYear, request.getPegawaiId(), totalDays, request.getTanggalSelesai());
        setPic(entity);
        // simpan cuti
        return repository.save(entity);
    }

    /**
     * Pengajuan cuti antara 1 juli s/d 31 desember maka selalu cek kuota tahun berjalan
     * 1. pengajuan diasumsikan pada tanggal 1 juli sampai 30 desember
     * 2. jika ada, ambil jatah cuti tahun berjalan
     * 3. jika tidak ada, batalkan
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @param entity  the entity containing the leave information.
     * @return CutiPegawai
     * @throws RuntimeException if the employee or leave type is unknown, or if leave quotas
     *                          are insufficient for the requested leave period.
     */
    public CutiPegawai between1JulAnd31Dec(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        int totalRemainingQuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalHariCuti, totalRemainingQuota, 0);
        applyAllocation(entity, res);

        setPic(entity);
        return repository.save(entity);
    }

    public CutiPegawai between30JunAnd1Jul(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        int prevKuota = cutiKuotaRepository
                .findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(request.getPegawaiId(), year - 1, request.getTanggalSelesai(), SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;

        id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        CutiKuotaAllocationResult res;
        if (prevKuota == 0) {
            res = CutiKuotaAllocator.allocate(totalHariCuti, 0, currentKuota);
        } else {
            int totalCutiJuni = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30))
                    - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30));
            res = CutiKuotaAllocator.allocate(totalHariCuti, prevKuota, currentKuota, totalCutiJuni);
        }

        applyAllocation(entity, res);

        setPic(entity);
        return repository.save(entity);
    }

    public void saveCutiNonTahunan(CutiPengajuanPostRequest request, CutiPegawai entity) {
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(
                        request.getPegawaiId(),
                        year - 1,
                        LocalDate.of(year, 6, 30),
                        SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElse(0);
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;
        id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule.check(totalHariCuti, totalRemainingQuota);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiIbadah())) {
            CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(currentKuota, 0, currentKuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatPakai0(0);
            entity.setRiwayatSisa0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
            entity.setRiwayatPakai1(res.getRiwayatPakai1());
            entity.setRiwayatSisa1(res.getRiwayatSisa1());
            entity.setKuotaAwal(prevKuota + currentKuota);
            entity.setKuotaAkhir(prevKuota);
        } else {
            entity.setKuotaAwal(totalRemainingQuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
        }

        repository.save(entity);
    }

    private void separateCutiWithNextYear(CutiPegawai entity, int currentYear, int nextYear, long pegawaiId, int totalDays) {
        int currentYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Tahun Cuti Tidak Ditemukan"));
        int nextYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, nextYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        int totalRemaining = currentYearRemaining + nextYearRemaining;

        id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule.check(totalDays, totalRemaining);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalDays, currentYearRemaining, nextYearRemaining);
        applyAllocation(entity, res);
    }

    private void separateCutiWithPreviousYear(CutiPegawai entity, int prevYear, int currentYear, long pegawaiId, int totalDays, LocalDate expiredDate) {
        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(pegawaiId, prevYear, expiredDate, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + currentYear + " tidak tersedia!"));

        int totalRemainingQuota = currentKuota + prevKuota;
        id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule.check(totalDays, totalRemainingQuota);

        CutiKuotaAllocationResult res = CutiKuotaAllocator.allocate(totalDays, prevKuota, currentKuota);
        applyAllocation(entity, res);
    }

    private void applyAllocation(CutiPegawai entity, CutiKuotaAllocationResult res) {
        entity.setRiwayatKuota0(res.getRiwayatKuota0());
        entity.setRiwayatPakai0(res.getRiwayatPakai0());
        entity.setRiwayatSisa0(res.getRiwayatSisa0());
        entity.setRiwayatKuota1(res.getRiwayatKuota1());
        entity.setRiwayatPakai1(res.getRiwayatPakai1());
        entity.setRiwayatSisa1(res.getRiwayatSisa1());
        entity.setKuotaAwal(res.getKuotaAwal());
        entity.setKuotaAkhir(res.getKuotaAkhir());
    }

    /**
     * Sets the PIC of a cuti based on the employee's level.
     * <p>
     * If the employee is a manager, the PIC is the supervisor of the SDM department.
     * Otherwise, it is the parent of the employee's job title.
     *
     * @param cutiPegawai the cuti to set the PIC for
     */
    private void setPic(CutiPegawai cutiPegawai) {
        // Get the job title of the employee
        Jabatan jabatan = cutiPegawai.getPegawai().getJabatan();

        // If the employee is a manager, set the PIC to the supervisor of the SDM department
        if (jabatan.getLevel().getId().equals(cutiProperties.getLevelManager())) {
            // Find the supervisor of the SDM department
            // Set the PIC of the cuti to the supervisor of the SDM department
            jabatanRepository.findById(cutiProperties.getSupervisorSdm()).ifPresent(cutiPegawai::setPicSaatIni);
        } else {
            // Set the PIC of the cuti to the parent of the employee's job title
            cutiPegawai.setPicSaatIni(jabatan.getParent());
        }
    }
}
