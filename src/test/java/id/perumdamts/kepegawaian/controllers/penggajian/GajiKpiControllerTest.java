package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.*;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiBatchService;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiKpi.GajiKpiQueryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiKpiControllerTest {

    @Mock
    private GajiKpiCommandService commandService;

    @Mock
    private GajiKpiQueryService queryService;

    @Mock
    private GajiKpiBatchService batchService;

    private GajiKpiController controller;

    @BeforeEach
    void setUp() {
        controller = new GajiKpiController(commandService, queryService, batchService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private GajiKpiResponse sampleResponse() {
        return new GajiKpiResponse(1L, "710100239", "2026-01", 4_619_579.0, 230_978.95);
    }

    @Test
    @DisplayName("GET /penggajian/kpi -> returns paged result")
    void index_returnsPageResult() {
        GajiKpiResponse res = sampleResponse();
        Page<GajiKpiResponse> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);
        when(queryService.findPage(any(GajiKpiIndexQuery.class))).thenReturn(page);

        ResponseEntity<PageResult<Page<GajiKpiResponse>>> response = controller.index(new GajiKpiIndexQuery());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().getContent().size());
        assertEquals("710100239", response.getBody().getData().getContent().getFirst().nipam());
    }

    @Test
    @DisplayName("GET /penggajian/kpi/list -> returns list result")
    void list_returnsListResult() {
        GajiKpiResponse res = sampleResponse();
        when(queryService.findAll(any(GajiKpiListRequest.class))).thenReturn(List.of(res));

        ResponseEntity<ListResult<GajiKpiResponse>> response = controller.list(new GajiKpiListRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    @DisplayName("GET /penggajian/kpi/{id} -> returns single result")
    void show_returnsSingleResult() {
        GajiKpiResponse res = sampleResponse();
        when(queryService.findById(1L)).thenReturn(Optional.of(res));

        ResponseEntity<SingleResult<GajiKpiResponse>> response = controller.show(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("710100239", response.getBody().getData().nipam());
    }

    @Test
    @DisplayName("POST /penggajian/kpi -> creates record")
    void create_returnsSavedResult() {
        GajiKpiPostRequest req = new GajiKpiPostRequest();
        req.setNipam("710100239");
        req.setPeriode("2026-01");
        req.setTunkin(4_619_579.0);
        req.setPph21Ter(230_978.95);

        when(commandService.save(any(GajiKpiPostRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, 1L));

        ResponseEntity<SavedResult<Long>> response = controller.create(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData());
    }

    @Test
    @DisplayName("PUT /penggajian/kpi/{id} -> updates record")
    void update_returnsSavedResult() {
        GajiKpiPutRequest req = new GajiKpiPutRequest();
        req.setNipam("710100239");
        req.setPeriode("2026-01");
        req.setTunkin(5_000_000.0);
        req.setPph21Ter(250_000.0);

        when(commandService.update(eq(1L), any(GajiKpiPutRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, 1L));

        ResponseEntity<SavedResult<Long>> response = controller.update(1L, req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData());
    }

    @Test
    @DisplayName("DELETE /penggajian/kpi/{id} -> deletes record")
    void delete_returnsDeletedResult() {
        when(commandService.delete(1L)).thenReturn(true);

        ResponseEntity<DeletedResult> response = controller.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getBody().getStatusText());
    }

    @Test
    @DisplayName("POST /penggajian/kpi/upload -> handles batch upload with AppwriteUser in SecurityContext")
    void upload_withAppwriteUser_resolvesUsernameAndCallsService() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2, 3});

        AppwriteUser appwriteUser = new AppwriteUser();
        appwriteUser.setName("admin_user");
        Authentication auth = new UsernamePasswordAuthenticationToken(appwriteUser, "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        GajiKpiUploadResponse uploadResponse = new GajiKpiUploadResponse("2026-01", 10, 8, 2);
        when(batchService.upload(file, "2026-01", "admin_user")).thenReturn(uploadResponse);

        ResponseEntity<SingleResult<GajiKpiUploadResponse>> response = controller.upload(file, "2026-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GajiKpiUploadResponse data = response.getBody().getData();
        assertEquals("2026-01", data.periode());
        assertEquals(10, data.totalRows());
        assertEquals(8, data.inserted());
        assertEquals(2, data.updated());
        verify(batchService).upload(file, "2026-01", "admin_user");
    }

    @Test
    @DisplayName("POST /penggajian/kpi/upload -> handles batch upload with standard principal name")
    void upload_withStandardAuth_resolvesPrincipalName() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2, 3});

        Authentication auth = new UsernamePasswordAuthenticationToken("custom_user", "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        GajiKpiUploadResponse uploadResponse = new GajiKpiUploadResponse("2026-01", 5, 5, 0);
        when(batchService.upload(file, "2026-01", "custom_user")).thenReturn(uploadResponse);

        ResponseEntity<SingleResult<GajiKpiUploadResponse>> response = controller.upload(file, "2026-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getData().totalRows());
        verify(batchService).upload(file, "2026-01", "custom_user");
    }

    @Test
    @DisplayName("POST /penggajian/kpi/upload -> fallback to system when no auth")
    void upload_withoutAuth_fallbackToSystem() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2, 3});

        GajiKpiUploadResponse uploadResponse = new GajiKpiUploadResponse("2026-01", 1, 1, 0);
        when(batchService.upload(file, "2026-01", "system")).thenReturn(uploadResponse);

        ResponseEntity<SingleResult<GajiKpiUploadResponse>> response = controller.upload(file, "2026-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(batchService).upload(file, "2026-01", "system");
    }

    @Test
    @DisplayName("GET /penggajian/kpi/template/download -> downloads template file")
    void downloadTemplate_returnsExcelResourceWithHeaders() {
        byte[] content = "test template content".getBytes();
        Resource mockResource = new ByteArrayResource(content);
        when(batchService.getTemplateResource()).thenReturn(mockResource);

        ResponseEntity<Resource> response = controller.downloadTemplate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("attachment; filename=\"Tunkin Template.xlsx\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                response.getHeaders().getContentType());
        assertNotNull(response.getBody());
        assertSame(mockResource, response.getBody());
    }
}
