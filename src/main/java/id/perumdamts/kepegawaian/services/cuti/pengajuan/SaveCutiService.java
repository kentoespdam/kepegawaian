package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveCutiService {
    private final CutiPegawaiRepository repository;
    private final ValidatePengajuanCutiService validatePengajuanCutiService;
    private final PegawaiRepository pegawaiRepository;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final HariLiburRepository hariLiburRepository;
    private final CutiJenisRepository cutiJenisRepository;

    /**
     * Processes a leave request for the next year, calculating total days and workdays,
     * validating leave quotas, and adjusting quota usage accordingly.
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @throws RuntimeException if the employee or leave type is unknown, or if leave quotas
     *                          are insufficient for the requested leave period.
     */
    public void forNextYear(CutiPengajuanPostRequest request) {
        int totalDays = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
        int totalHariCuti = totalDays - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());

        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
        CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId()).orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));
        CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId()).orElse(null);
        Jabatan atasanLangsung = new Jabatan(pegawai.getJabatan().getParent().getId());

        CutiPegawai entity = CutiPengajuanPostRequest.toEntity(request, pegawai, jenisCuti, subJenisCuti, atasanLangsung);
        entity.setJumlahHari(totalDays);
        entity.setJumlahHariKerja(totalHariCuti);

        int currentYear = request.getTanggalMulai().getYear();
        CutiKuota currentYearQuota = cutiKuotaRepository.findByPegawai_IdAndTahun(request.getPegawaiId(), currentYear - 1)
                .orElseThrow(() -> new RuntimeException("Tahun Cuti Tidak Ditemukan"));
        entity.setRiwayatKuota0(currentYearQuota.getSisaKuota());
        CutiKuota nextYearQuota = cutiKuotaRepository.findByPegawai_IdAndTahun(request.getPegawaiId(), currentYear)
                .orElse(null);

        int totalRemainingQuota = currentYearQuota.getSisaKuota() + (nextYearQuota == null ? 0 : nextYearQuota.getSisaKuota());
        validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);

        if (totalRemainingQuota < totalHariCuti) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        if (currentYearQuota.getSisaKuota() >= totalHariCuti) {
            entity.setRiwayatKuota0(currentYearQuota.getSisaKuota());
            entity.setRiwayatPakai0(totalHariCuti);
            entity.setRiwayatSisa0(currentYearQuota.getSisaKuota() - totalHariCuti);

            currentYearQuota.setKuotaTerpakai(currentYearQuota.getKuotaTerpakai() + totalHariCuti);
            currentYearQuota.setSisaKuota(currentYearQuota.getSisaKuota() - totalHariCuti);
        } else {
            entity.setRiwayatKuota0(currentYearQuota.getSisaKuota());
            entity.setRiwayatPakai0(currentYearQuota.getSisaKuota());
            entity.setRiwayatSisa0(0);

            int sisaKuota = totalHariCuti - currentYearQuota.getSisaKuota();

            if (nextYearQuota == null) {
                throw new RuntimeException("Kuota Cuti Tahun depan belum dibuat!");
            } else if (nextYearQuota.getSisaKuota() < sisaKuota) {
                throw new RuntimeException("Kuota Cuti Tahun depan tidak tersedia! sisa kuota: " + nextYearQuota.getSisaKuota() + " hari");
            }

            entity.setRiwayatKuota1(nextYearQuota.getSisaKuota());
            entity.setRiwayatPakai1(sisaKuota);
            entity.setRiwayatSisa1(nextYearQuota.getSisaKuota() - sisaKuota);

            nextYearQuota.setKuotaTerpakai(nextYearQuota.getKuotaTerpakai() + sisaKuota);
            nextYearQuota.setSisaKuota(nextYearQuota.getSisaKuota() - sisaKuota);

            currentYearQuota.setKuotaTerpakai(currentYearQuota.getSisaKuota());
        }

        repository.save(entity);
        cutiKuotaRepository.save(currentYearQuota);
        if (nextYearQuota != null) cutiKuotaRepository.save(nextYearQuota);

    }


    public void overlappingYear(CutiPengajuanPostRequest request) {

    }

    public void between1JanAnd30Jun(CutiPengajuanPostRequest request) {

    }

    public void between1JulAnd31Dec(CutiPengajuanPostRequest request) {

    }

    /**
     * Processes a leave request overlapping the mid-year transition, adjusting leave quotas accordingly.
     * Validates the request, checks and deducts from the current and previous year's leave quotas.
     *
     * @param request the leave request containing details such as employee ID, leave type,
     *                start and end dates, and sub-type of leave.
     * @throws RuntimeException if the leave quotas are insufficient or if required data is missing.
     */
//    @Transactional
    public void between30JunAnd1Jul(CutiPengajuanPostRequest request) {
        validatePengajuanCutiService.validate(request);

        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow();
        CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId()).orElseThrow();
        CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId()).orElse(null);
        Jabatan atasanLangsung = pegawai.getJabatan().getParent();

        CutiPegawai entity = CutiPengajuanPostRequest.toEntity(request, pegawai, jenisCuti, subJenisCuti, atasanLangsung);
        int totalHari = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
        int totalCutiDiambil = totalHari - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());
        entity.setJumlahHari(totalHari);
        entity.setJumlahHariKerja(totalCutiDiambil);

        int year = request.getTanggalMulai().getYear();
        CutiKuota currentYearKuota = cutiKuotaRepository.findByPegawai_IdAndTahun(request.getPegawaiId(), year).orElseThrow();
        entity.setRiwayatKuota1(currentYearKuota.getSisaKuota());
        CutiKuota previousYearKuota = cutiKuotaRepository.findByPegawai_IdAndTahun(request.getPegawaiId(), year - 1).orElse(null);

        int totalRemainingQuota = currentYearKuota.getSisaKuota() + (previousYearKuota == null ? 0 : previousYearKuota.getSisaKuota());
        validatePengajuanCutiService.validateMinimalCuti(totalCutiDiambil, totalRemainingQuota);

        entity.setKuotaAwal(totalRemainingQuota);
        entity.setKuotaAkhir(totalRemainingQuota - totalCutiDiambil);

        if (totalRemainingQuota < totalCutiDiambil) {
            throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
        }

        // jika kuota tahun lalu tidak ada maka ambil tahun ini
        if (previousYearKuota == null || previousYearKuota.getSisaKuota() == 0) {
            entity.setRiwayatPakai1(totalCutiDiambil);
            entity.setRiwayatSisa1(currentYearKuota.getSisaKuota() - totalCutiDiambil);

            currentYearKuota.setKuotaTerpakai(currentYearKuota.getKuotaTerpakai() + totalCutiDiambil);
            currentYearKuota.setSisaKuota(currentYearKuota.getSisaKuota() - totalCutiDiambil);

        } else {
            entity.setRiwayatKuota0(previousYearKuota.getSisaKuota());

            int totalCutiJuni = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30))
                    - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), DateHelper.generateDate(year, 6, 30));
            int totalCutiJuli = DateHelper.countWeekdaysBetween(DateHelper.generateDate(year, 7, 1), request.getTanggalSelesai())
                    - hariLiburRepository.countByTanggalBetween(DateHelper.generateDate(year, 7, 1), request.getTanggalSelesai());


            // jika kuota tahun lalu tidak mencukupi total cuti juni
            if (previousYearKuota.getSisaKuota() < totalCutiJuni) {
                int prevTotalCutiJuni = totalCutiJuni;
                totalCutiJuni = previousYearKuota.getSisaKuota();
                int sisaHariKerjaJuni = prevTotalCutiJuni - totalCutiJuni;
                totalCutiJuli = totalCutiJuli + sisaHariKerjaJuni;

                if (totalCutiJuli > currentYearKuota.getSisaKuota())
                    throw new RuntimeException("Kuota Cuti Tahun Ini tidak tersedia! sisa kuota: " + currentYearKuota.getSisaKuota() + " hari");
            }

            entity.setRiwayatPakai0(totalCutiJuni);
            entity.setRiwayatSisa0(previousYearKuota.getSisaKuota() - totalCutiJuni);
            entity.setRiwayatPakai1(totalCutiJuli);
            entity.setRiwayatSisa1(currentYearKuota.getSisaKuota() - totalCutiJuli);

            previousYearKuota.setKuotaTerpakai(previousYearKuota.getKuotaTerpakai() + totalCutiJuni);
            previousYearKuota.setSisaKuota(previousYearKuota.getSisaKuota() - totalCutiJuni);

            currentYearKuota.setKuotaTerpakai(currentYearKuota.getKuotaTerpakai() + totalCutiJuli);
            currentYearKuota.setSisaKuota(currentYearKuota.getSisaKuota() - totalCutiJuli);

        }

        repository.save(entity);
        cutiKuotaRepository.save(currentYearKuota);
        if (previousYearKuota != null) cutiKuotaRepository.save(previousYearKuota);
    }


}
