package id.perumdamts.kepegawaian.helpers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Slf4j
class DateHelperTest {

    @Test
    void localDateToString() {
    }

    @Test
    void countWeekdaysBetweenTest() {
        LocalDate startDate = LocalDate.of(2025, 6, 20);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        log.info("mulai: {}, sampai: {}", startDate, endDate.plusDays(1));

        int i = DateHelper.countWeekdaysBetween(startDate, endDate);
        log.info("i: {}", i);
    }

    @Test
    void cekTanggal() {
        LocalDate tgl = LocalDate.of(2025, 7, 1);
        LocalDate tgl2 = LocalDate.of(2025, 7, 1);
        // cek tgl is less than equal to tgl2
        log.info("cek: {}", tgl.isAfter(tgl2));
    }

    @Test
    void chekKuotaCuti() {
        int totalCuti = 5;
        int sisaJuni = 2;
        int sisaJuli = 12;

        int totalHariKerjaJuni = 3;
        int totalHariKerjaJuli = 2;

        log.info("before check");
        log.info("total cuti: {}", totalCuti);
        log.info("sisa juni: {}", sisaJuni);
        log.info("sisa juli: {}", sisaJuli);
        log.info("total hari kerja juni: {}", totalHariKerjaJuni);
        log.info("total hari kerja juli: {}", totalHariKerjaJuli);

        if (sisaJuni < totalHariKerjaJuni) {
            int prevTotalHariKerjaJuni = totalHariKerjaJuni;
            totalHariKerjaJuni = sisaJuni;
            int sisaHariKerjaJuni = prevTotalHariKerjaJuni - totalHariKerjaJuni;
            log.info("sisa hari kerja juni: {}", sisaHariKerjaJuni);
            totalHariKerjaJuli = totalHariKerjaJuli + sisaHariKerjaJuni;
        }

        log.info("After Check");
        log.info("total cuti: {}", totalCuti);
        log.info("sisa juni: {}", sisaJuni);
        log.info("sisa juli: {}", sisaJuli);
        log.info("total hari kerja juni: {}", totalHariKerjaJuni);
        log.info("total hari kerja juli: {}", totalHariKerjaJuli);
    }
}