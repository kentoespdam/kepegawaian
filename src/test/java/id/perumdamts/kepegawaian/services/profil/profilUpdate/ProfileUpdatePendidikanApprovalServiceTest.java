package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ADR-0035 approval flow: approve → disetujui=true + stamp approver;
 * reject (UPDATE) → kolom approval ikut dikembalikan ke revisi sebelumnya.
 */
@ExtendWith(MockitoExtension.class)
class ProfileUpdatePendidikanApprovalServiceTest {

    @Mock private RevInfoService revInfoService;
    @Mock private PendidikanRepository repository;
    @Mock private ChangedStatusResolver resolver;

    @InjectMocks private ProfileUpdatePendidikanApprovalService service;

    private ProfileUpdate pendingUpdate() {
        return ProfileUpdate.builder()
                .revId("42")
                .actionType(RevisionMetadata.RevisionType.UPDATE)
                .build();
    }

    @Test
    void approveStampsDisetujuiAndApprover() {
        Pendidikan pendidikan = new Pendidikan();
        when(repository.findById(42L)).thenReturn(Optional.of(pendidikan));
        when(resolver.currentUserId()).thenReturn("approver-1");

        service.changeHandler(pendingUpdate(), EProfileUpdateApproval.APPROVED);

        assertTrue(pendidikan.getDisetujui());
        assertNotNull(pendidikan.getTanggalDisetujui());
        assertEquals("approver-1", pendidikan.getDisetujuiOleh());
        assertEquals(Boolean.FALSE, pendidikan.getChangedStatus());
        verify(repository).save(pendidikan);
    }

    @Test
    void rejectRestoresApprovalColumnsFromPreviousRevision() {
        LocalDateTime pengajuan = LocalDateTime.of(2026, 1, 1, 9, 0);
        LocalDateTime disetujuiTgl = LocalDateTime.of(2026, 1, 2, 9, 0);
        Pendidikan prev = new Pendidikan();
        prev.setBiodata(new Biodata("NIK"));
        prev.setJenjangPendidikan(new id.perumdamts.kepegawaian.entities.master.JenjangPendidikan());
        prev.setDisetujui(true);
        prev.setTanggalPengajuan(pengajuan);
        prev.setTanggalDisetujui(disetujuiTgl);
        prev.setDisetujuiOleh("sdm-orig");
        when(revInfoService.findLatestRevision(any(), any(Long.class))).thenReturn(List.of(prev));

        service.changeHandler(pendingUpdate(), EProfileUpdateApproval.REJECT);

        verify(repository).rollbackPrevVersion(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                eq(Boolean.TRUE), eq(pengajuan), eq(disetujuiTgl), eq("sdm-orig"), any());
    }
}
