package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiDto;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.ForbiddenException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterQueryService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.SlipGajiPdfGenerator;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GajiBatchMasterControllerSlipGajiTest {

    private final GajiBatchMasterQueryService queryService = mock(GajiBatchMasterQueryService.class);
    private final GajiBatchPotonganTambahanBatchService batchPotonganService = mock(GajiBatchPotonganTambahanBatchService.class);
    private final SlipGajiPdfGenerator pdfGenerator = mock(SlipGajiPdfGenerator.class);
    private final PegawaiRepository pegawaiRepository = mock(PegawaiRepository.class);
    private final GajiBatchMasterController controller = new GajiBatchMasterController(queryService, batchPotonganService, pdfGenerator, pegawaiRepository);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void downloadSlipGaji_adminRole_succeeds() {
        setAuth("admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SlipGajiDto slipDto = createSampleSlip(100L, "12345");
        when(queryService.getSlipGaji(1L)).thenReturn(slipDto);
        when(pdfGenerator.generatePdf(slipDto)).thenReturn(new byte[]{0x25, 0x50, 0x44, 0x46});

        ResponseEntity<Resource> response = controller.downloadSlipGaji(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
    }

    @Test
    void downloadSlipGaji_selfOwner_succeeds() {
        AppwriteUser user = new AppwriteUser();
        user.set$id("100");
        setAuthUser(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SlipGajiDto slipDto = createSampleSlip(100L, "12345");
        when(queryService.getSlipGaji(1L)).thenReturn(slipDto);
        when(pdfGenerator.generatePdf(slipDto)).thenReturn(new byte[]{0x25, 0x50, 0x44, 0x46});

        ResponseEntity<Resource> response = controller.downloadSlipGaji(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadSlipGaji_otherPegawai_throwsForbidden() {
        AppwriteUser user = new AppwriteUser();
        user.set$id("999");
        setAuthUser(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SlipGajiDto slipDto = createSampleSlip(100L, "12345");
        when(queryService.getSlipGaji(1L)).thenReturn(slipDto);

        assertThrows(ForbiddenException.class, () -> controller.downloadSlipGaji(1L));
    }

    @Test
    void downloadSlipGaji_batchNotFinished_throwsBadRequest() {
        setAuth("admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(queryService.getSlipGaji(anyLong())).thenThrow(new BadRequestException("Slip gaji hanya dapat diunduh jika status batch sudah FINISHED"));

        assertThrows(BadRequestException.class, () -> controller.downloadSlipGaji(1L));
    }

    @Test
    void downloadSlipGaji_notFound_throwsNotFound() {
        setAuth("admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(queryService.getSlipGaji(anyLong())).thenThrow(new NotFoundException("Data penggajian tidak ditemukan"));

        assertThrows(NotFoundException.class, () -> controller.downloadSlipGaji(1L));
    }

    private void setAuth(String name, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(name, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void setAuthUser(AppwriteUser user, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private SlipGajiDto createSampleSlip(Long pegawaiId, String nipam) {
        return new SlipGajiDto(
                1L,
                "202501-001",
                "2025-01",
                pegawaiId,
                nipam,
                "Bagus Pegawai",
                "Staf IT",
                "III/a",
                "- - -",
                List.of(),
                List.of(),
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                List.of(),
                List.of(),
                0.0,
                0.0,
                0.0
        );
    }
}
