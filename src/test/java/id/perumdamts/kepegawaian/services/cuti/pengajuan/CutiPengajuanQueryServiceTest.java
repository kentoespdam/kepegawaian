package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiPengajuanQueryRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-oo7y (opsi B): index GET /cuti/pengajuan di-scope per-principal —
 * non-privileged hanya melihat cuti milik sendiri (pegawaiId dipaksa = id principal),
 * HRD/ADMIN bebas memfilter semua pegawai.
 */
@ExtendWith(MockitoExtension.class)
class CutiPengajuanQueryServiceTest {

    @Mock private CutiPengajuanQueryRepository queryRepository;
    @Mock private HariLiburRepository hariLiburRepository;
    @Mock private CutiOwnershipService ownershipService;

    @InjectMocks private CutiPengajuanQueryService service;

    private CutiPengajuanRequest requestWithPegawaiId(Long pegawaiId) {
        CutiPengajuanRequest request = new CutiPengajuanRequest();
        request.setPegawaiId(pegawaiId);
        return request;
    }

    @Test
    void nonPrivilegedIndexForcedToOwnPegawaiId() {
        // USER mengirim filter pegawaiId=54 (orang lain) -> harus di-override ke id sendiri
        when(ownershipService.isPrivileged()).thenReturn(false);
        when(ownershipService.currentPegawaiId()).thenReturn(394L);
        when(queryRepository.pageQuery(any())).thenReturn(Page.empty());

        service.findPage(requestWithPegawaiId(54L));

        ArgumentCaptor<CutiPengajuanRequest> captor = ArgumentCaptor.forClass(CutiPengajuanRequest.class);
        verify(queryRepository).pageQuery(captor.capture());
        assertEquals(394L, captor.getValue().getPegawaiId(),
                "non-privileged: pegawaiId query harus dipaksa ke id principal");
    }

    @Test
    void privilegedIndexKeepsRequestedFilter() {
        // HRD/ADMIN bebas melihat semua pegawai — filter pegawaiId dari query dihormati
        when(ownershipService.isPrivileged()).thenReturn(true);
        when(queryRepository.pageQuery(any())).thenReturn(Page.empty());

        service.findPage(requestWithPegawaiId(54L));

        ArgumentCaptor<CutiPengajuanRequest> captor = ArgumentCaptor.forClass(CutiPengajuanRequest.class);
        verify(queryRepository).pageQuery(captor.capture());
        assertEquals(54L, captor.getValue().getPegawaiId(),
                "privileged: filter pegawaiId dari request tidak boleh diubah");
    }
}
