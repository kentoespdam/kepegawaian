package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterSelfQuery;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterQueryService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.SlipGajiPdfGenerator;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GajiBatchMasterControllerSelfTest {

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
    void getMyGajiBatchMasterHistory_validUser_succeeds() {
        AppwriteUser user = new AppwriteUser();
        user.set$id("100");
        setAuthUser(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Pegawai pegawai = new Pegawai();
        pegawai.setId(100L);
        when(pegawaiRepository.findById(100L)).thenReturn(Optional.of(pegawai));

        Page<id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse> emptyPage = new PageImpl<>(List.of());
        when(queryService.findHistoryByPegawaiId(eq(100L), any(GajiBatchMasterSelfQuery.class))).thenReturn(emptyPage);

        GajiBatchMasterSelfQuery query = new GajiBatchMasterSelfQuery();
        ResponseEntity<?> response = controller.getMyGajiBatchMasterHistory(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getMyGajiBatchMasterHistory_devUser_throwsNotFound() {
        AppwriteUser user = new AppwriteUser();
        user.set$id("DEV");
        setAuthUser(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        GajiBatchMasterSelfQuery query = new GajiBatchMasterSelfQuery();
        assertThrows(NotFoundException.class, () -> controller.getMyGajiBatchMasterHistory(query));
    }

    @Test
    void getMyGajiBatchMasterHistory_unknownPegawai_throwsNotFound() {
        AppwriteUser user = new AppwriteUser();
        user.set$id("999");
        setAuthUser(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(pegawaiRepository.findById(999L)).thenReturn(Optional.empty());

        GajiBatchMasterSelfQuery query = new GajiBatchMasterSelfQuery();
        assertThrows(NotFoundException.class, () -> controller.getMyGajiBatchMasterHistory(query));
    }

    private void setAuthUser(AppwriteUser user, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
