package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.helpers.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SaveKlaimCutiServiceTest {
    private final List<LocalDate> listHari = new ArrayList<>();

    @BeforeEach
    void setUp() {
        listHari.add(LocalDate.of(2026, 1, 21));
        listHari.add(LocalDate.of(2026, 1, 19));
        listHari.add(LocalDate.of(2026, 1, 22));
    }

    @Test
    void testList() {
        log.info(listHari.toString());
        List<LocalDate> workingDays = DateHelper.getWorkingDays(listHari, List.of());
        log.info(workingDays.toString());
    }
}