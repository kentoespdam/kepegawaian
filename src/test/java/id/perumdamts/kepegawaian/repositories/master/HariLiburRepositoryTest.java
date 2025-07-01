package id.perumdamts.kepegawaian.repositories.master;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class HariLiburRepositoryTest {
    @Autowired
    private HariLiburRepository repository;

    @Test
    void test() {
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        Integer i = repository.countByTanggalBetween(startDate, endDate);
        log.info("i: {}", i);
    }
}