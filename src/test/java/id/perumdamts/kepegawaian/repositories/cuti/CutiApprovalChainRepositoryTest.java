package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class CutiApprovalChainRepositoryTest {
    @Autowired
    private CutiApprovalChainRepository repository;

    @Test
    void findCutiApprovalPegawai() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CutiPegawai> cutiApprovalPegawai = repository.findCutiApprovalPegawai(2025, 1L, 48L, EApprovalCutiStatus.APPROVED, pageable);
        assertNotNull(cutiApprovalPegawai);
        assertEquals(1, cutiApprovalPegawai.getTotalElements());
    }
}