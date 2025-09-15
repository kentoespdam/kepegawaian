package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.dto.cuti.kuota.SisaCutiRecord;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaveCutiService {
    private final CutiPegawaiRepository repository;
    private final ValidatePengajuanCutiService validatePengajuanCutiService;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final HariLiburRepository hariLiburRepository;
    private final JabatanRepository jabatanRepository;
    private final DefConfig defConfig;

    @Value("${custom.levelJabatan.manager}")
    private Long levelManager;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdmId;

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
        // total cuti yang diambil
        int totalHariCuti = entity.getJumlahHariKerja();

        // ambil tahun
        int year = request.getTanggalMulai().getYear();

        // ambil jatah cuti tahun berjalan
        int totalRemainingQuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        // validasi minimal cuti
        validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

        // jika kuota tahun berjalan tidak mencukupi maka batalkan
        if (totalRemainingQuota < totalHariCuti) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        // set kuota awal dan akhir
        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);

        // set riwayat kuota tahun berjalan
        entity.setRiwayatKuota0(totalRemainingQuota);
        entity.setRiwayatPakai0(totalHariCuti);
        entity.setRiwayatSisa0(totalRemainingQuota - totalHariCuti);

        setPic(entity);
        // simpan cuti
        return repository.save(entity);
    }

    /**
     * Pengajuan Cuti menyebrang tanggal 30 juni sampai 1 juli
     * 1. pengajuan diasumsikan pada akhir bulan juni tahun berjalan sampai awal bulan juli
     * 2. jika ada, ambil jatah cuti tahun lalu untuk bulan juni dan ambil jatah cuti tahun berjalan untuk bulan juli
     * 3. atau jika ada, ambil jatah cuti tahun depan untuk bulan juni dan juli
     * 4. jika jumlah cuti tahun berjalan dan/atau jatah cuti tahun depan tidak ada maka batalkan
     * 5. cuti tahun berjalan harus ambil dari jatah cuti tahun berjalan
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @param entity  the entity containing the leave information.
     * @return CutiPegawai
     * @throws RuntimeException if the leave quotas are insufficient or if required data is missing.
     */
    public CutiPegawai between30JunAnd1Jul(CutiPengajuanPostRequest request, CutiPegawai entity) {
        // total cuti yang diambil
        int totalHariCuti = entity.getJumlahHariKerja();
        int year = request.getTanggalMulai().getYear();

        // ambil jatah cuti tahun lalu
        int prevKuota = cutiKuotaRepository
                .findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(request.getPegawaiId(), year - 1, request.getTanggalSelesai(), SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        // ambil jatah cuti tahun berjalan
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        // total jatah cuti yang tersedia
        int totalRemainingQuota = currentKuota + prevKuota;

        // validasi minimal cuti
        validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

        // jika kuota tidak mencukupi maka batalkan
        if (totalRemainingQuota < totalHariCuti) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        // set kuota awal dan akhir
        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
        entity.setRiwayatKuota0(prevKuota);
        entity.setRiwayatKuota1(currentKuota);

        // jika kuota tahun lalu tidak ada maka ambil tahun ini
        if (prevKuota == 0) {
            entity.setRiwayatPakai1(totalHariCuti);
            entity.setRiwayatSisa1(currentKuota - totalHariCuti);
        } else {
            // hitung total cuti juni dan juli
            int totalCutiJuni = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30))
                    - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30));
            int totalCutiJuli = DateHelper.countWeekdaysBetween(DateHelper.generateDate(year, 7, 1), request.getTanggalSelesai())
                    - hariLiburRepository.countByTanggalBetween(DateHelper.generateDate(year, 7, 1), request.getTanggalSelesai());

            // jika kuota tahun lalu tidak mencukupi total cuti juni
            if (prevKuota < totalCutiJuni) {
                int prevTotalCutiJuni = totalCutiJuni;
                totalCutiJuni = prevKuota;
                int sisaHariKerjaJuni = prevTotalCutiJuni - totalCutiJuni;
                totalCutiJuli = totalCutiJuli + sisaHariKerjaJuni;

                if (totalCutiJuli > currentKuota)
                    throw new RuntimeException("Kuota Cuti Tahun Ini tidak tersedia! sisa kuota: " + currentKuota + " hari");
            }

            // set riwayat pakai dan sisa
            entity.setRiwayatPakai0(totalCutiJuni);
            entity.setRiwayatSisa0(prevKuota - totalCutiJuni);
            entity.setRiwayatPakai1(totalCutiJuli);
            entity.setRiwayatSisa1(currentKuota - totalCutiJuli);
        }

        setPic(entity);
        // simpan entity
        return repository.save(entity);
    }

    /**
     * Pengajuan Cuti selain cuti tahunan
     * 1. pengajuan diasumsikan jenis cuti bukan tahunan
     * 2. total jatah cuti yang tersedia adalah jatah cuti tahun berjalan ditambah sisa jatah cuti
     * tahun lalu, jika ada
     * 3. cek apakah kuota cuti yang diinginkan kurang dari jatah cuti yang tersedia
     * 4. jika kurang, maka batalkan pengajuan
     * 5. jika lebih, maka pakai jatah cuti tahun berjalan
     * 6. jika jatah cuti tahun berjalan tidak mencukupi, maka pakai jatah cuti tahun lalu
     * 7. simpan entity
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @param entity  the entity containing the leave information.
     * @throws RuntimeException if the leave quotas are insufficient or if required data is missing.
     */
    public void saveCutiNonTahunan(CutiPengajuanPostRequest request, CutiPegawai entity) {
        // total cuti yang diambil
        int totalHariCuti = entity.getJumlahHariKerja();

        // ambil tahun
        int year = request.getTanggalMulai().getYear();

        // ambil jatah cuti tahun lalu
        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(
                        request.getPegawaiId(),
                        year - 1,
                        LocalDate.of(year, 6, 30),
                        SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElse(0);
        // ambil jatah cuti tahun berjalan
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(request.getPegawaiId(), year, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + year + " tidak tersedia!"));

        // total jatah cuti yang tersedia
        int totalRemainingQuota = currentKuota + prevKuota;
        // validasi minimal cuti
        validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

        if (request.getJenisCutiId().equals(defConfig.getJenisCutiIbadah())) {
            entity.setKuotaAkhir(totalRemainingQuota - currentKuota);
            entity.setRiwayatPakai1(currentKuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
            entity.setRiwayatPakai1(currentKuota);
            entity.setRiwayatSisa1(0);
        } else {
            entity.setKuotaAwal(totalRemainingQuota);
            entity.setKuotaAwal(totalRemainingQuota);
            entity.setRiwayatKuota0(prevKuota);
            entity.setRiwayatKuota1(currentKuota);
        }

        repository.save(entity);
    }

    /**
     * Separate the cuti with the next year.
     *
     * @param entity      the cuti entity.
     * @param currentYear the current year.
     * @param nextYear    the next year.
     * @param pegawaiId   the employee ID.
     * @param totalDays   the total days of cuti.
     * @throws RuntimeException if the cuti quotas are insufficient or if required data is missing.
     */
    private void separateCutiWithNextYear(CutiPegawai entity, int currentYear, int nextYear, long pegawaiId, int totalDays) {
        // Kuota cuti tahun berjalan
        int currentYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Tahun Cuti Tidak Ditemukan"));
        // Kuota cuti tahun depan
        int nextYearRemaining = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, nextYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        // Total kuota cuti yang tersedia
        int totalRemaining = currentYearRemaining + nextYearRemaining;

        // Validasi minimal cuti
        validatePengajuanCutiService.validateMinimalCuti(totalDays, totalRemaining);

        // Jika ada kuota cuti tahun berjalan yang tersedia, maka pakai kuota cuti tahun berjalan
        int remainingAfterCurrentYear = totalDays - currentYearRemaining;
        if (remainingAfterCurrentYear > 0) {
            if (nextYearRemaining < remainingAfterCurrentYear) {
                throw new RuntimeException("Kuota Cuti Tahun depan tidak tersedia! sisa kuota: " + nextYearRemaining + " hari");
            }

            entity.setRiwayatKuota0(currentYearRemaining);
            entity.setRiwayatPakai0(currentYearRemaining);
            entity.setRiwayatSisa0(0);
            entity.setRiwayatKuota1(nextYearRemaining);
            entity.setRiwayatPakai1(remainingAfterCurrentYear);
            entity.setRiwayatSisa1(nextYearRemaining - remainingAfterCurrentYear);
        } else {
            entity.setRiwayatKuota0(currentYearRemaining);
            entity.setRiwayatPakai0(totalDays);
            entity.setRiwayatSisa0(currentYearRemaining - totalDays);
        }

        // Simpan entity
        entity.setKuotaAwal(totalRemaining);
        entity.setKuotaAkhir(totalRemaining - totalDays);
    }


    /**
     * Separates the cuti with the previous year.
     *
     * @param entity      the cuti entity.
     * @param prevYear    the previous year.
     * @param currentYear the current year.
     * @param pegawaiId   the employee ID.
     * @param totalDays   the total days of cuti.
     * @param expiredDate the expired date of previous year cuti quota.
     * @throws RuntimeException if the cuti quotas are insufficient or if required data is missing.
     */
    private void separateCutiWithPreviousYear(CutiPegawai entity, int prevYear, int currentYear, long pegawaiId, int totalDays, LocalDate expiredDate) {
        // Ambil jatah cuti tahun lalu
        int prevKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahunAndExpiredGreaterThan(pegawaiId, prevYear, expiredDate, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota).orElse(0);

        // Ambil jatah cuti tahun berjalan
        int currentKuota = cutiKuotaRepository.findRecordByPegawai_IdAndTahun(pegawaiId, currentYear, SisaCutiRecord.class)
                .map(SisaCutiRecord::sisaKuota)
                .orElseThrow(() -> new RuntimeException("Kuota Cuti Tahun " + currentYear + " tidak tersedia!"));

        // Total jatah cuti yang tersedia
        int totalRemainingQuota = currentKuota + prevKuota;
        // Validasi minimal cuti
        validatePengajuanCutiService.validateMinimalCuti(totalDays, totalRemainingQuota);

        // Cek apakah ada jatah cuti tahun lalu yang tersedia
        if (totalRemainingQuota < totalDays) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        // Cek apakah ada jatah cuti tahun berjalan yang tersedia
        int remainingDays = totalDays - prevKuota;
        if (remainingDays > 0) {
            if (currentKuota < remainingDays) {
                throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota tahun berjalan: " + currentKuota + " hari");
            }

            // Set riwayat pakai dan sisa
            entity.setRiwayatPakai0(prevKuota);
            entity.setRiwayatSisa0(0);
            entity.setRiwayatPakai1(remainingDays);
            entity.setRiwayatSisa1(currentKuota - remainingDays);
        } else {
            // Set riwayat pakai dan sisa
            entity.setRiwayatPakai0(totalDays);
            entity.setRiwayatSisa0(prevKuota - totalDays);
        }

        // Set riwayat kuota
        entity.setRiwayatKuota0(prevKuota);
        entity.setRiwayatKuota1(currentKuota);

        // Set kuota awal dan akhir
        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalDays);
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
        if (jabatan.getLevel().getId().equals(levelManager)) {
            // Find the supervisor of the SDM department
            // Set the PIC of the cuti to the supervisor of the SDM department
            jabatanRepository.findById(supervisorSdmId).ifPresent(cutiPegawai::setPicSaatIni);
        } else {
            // Set the PIC of the cuti to the parent of the employee's job title
            cutiPegawai.setPicSaatIni(jabatan.getParent());
        }
    }
}
