package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ADR-0035: disetujui ditentukan server berdasarkan role — HRD/ADMIN auto-approve + stamp,
 * selain itu tetap false (masuk antrian). Unit test tanpa DB (Mockito).
 */
@ExtendWith(MockitoExtension.class)
class PendidikanCommandServiceTest {

    @Mock private PendidikanRepository repository;
    @Mock private BiodataRepository biodataRepository;
    @Mock private JenjangPendidikanRepository jenjangPendidikanRepository;
    @Mock private LampiranProfilCommandService lampiranProfilCommandService;
    @Mock private ProfileUpdateService profileUpdateService;
    @Mock private ChangedStatusResolver resolver;

    @InjectMocks private PendidikanCommandService service;

    private PendidikanPostRequest request() {
        PendidikanPostRequest request = new PendidikanPostRequest();
        request.setBiodataId("3273012345678901");
        request.setJenjangPendidikanId(4L);
        request.setInstitusi("UGM");
        request.setTahunMasuk(2010);
        return request;
    }

    private void stubLookups() {
        when(biodataRepository.findById(any())).thenReturn(Optional.of(new Biodata("3273012345678901")));
        when(jenjangPendidikanRepository.findById(any())).thenReturn(Optional.of(new JenjangPendidikan()));
        when(repository.findAnyByUniqueKey(any(), any(), any())).thenReturn(Optional.empty());
    }

    private Pendidikan saveAndCapture() {
        ArgumentCaptor<Pendidikan> captor = ArgumentCaptor.forClass(Pendidikan.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        service.create(request());
        return captor.getValue();
    }

    @Test
    void hrdWriteAutoApprovesAndStamps() {
        when(resolver.requiresApproval()).thenReturn(false);
        when(resolver.currentUserId()).thenReturn("hrd-1");
        stubLookups();

        Pendidikan saved = saveAndCapture();

        assertTrue(saved.getDisetujui(), "HRD write must auto-approve");
        assertNotNull(saved.getTanggalDisetujui(), "HRD write must stamp approval date");
        assertNotNull(saved.getTanggalPengajuan(), "tanggalPengajuan must be set on create");
        assertFalse(saved.getChangedStatus(), "HRD write is stable (no approval queue)");
        assertEquals("hrd-1", saved.getDisetujuiOleh());
    }

    @Test
    void selfServiceWriteStaysUnapproved() {
        when(resolver.requiresApproval()).thenReturn(true);
        stubLookups();

        Pendidikan saved = saveAndCapture();

        assertFalse(saved.getDisetujui(), "self-service write must stay disetujui=false");
        assertNull(saved.getTanggalDisetujui(), "no approval stamp while pending");
        assertNull(saved.getDisetujuiOleh(), "no approver while pending");
        assertNotNull(saved.getTanggalPengajuan(), "tanggalPengajuan must be set on create");
        assertTrue(saved.getChangedStatus(), "self-service write enters approval queue");
    }
}
