package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.history.RevisionMetadata;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test untuk routing changedStatus {@link BiodataCommandService#patchBiodata}
 * (ADR-0038): konteks endpoint menentukan apakah masuk approval queue,
 * bukan role principal.
 */
@ExtendWith(MockitoExtension.class)
class BiodataCommandServicePatchTest {

    @Mock private BiodataRepository repository;
    @Mock private JenjangPendidikanRepository jenjangPendidikanRepository;
    @Mock private PendidikanCommandService pendidikanCommandService;
    @Mock private KartuIdentitasCommandService kartuIdentitasCommandService;
    @Mock private ProfileUpdateService profileUpdateService;
    @Mock private ChangedStatusResolver resolver;
    @Mock private FileUploadUtil fileUploadUtil;
    @InjectMocks private BiodataCommandService service;

    private static final String NIK = "1234567890";

    @Test
    void patchBiodata_requiresApprovalTrue_entersApprovalQueue() {
        Biodata entity = new Biodata(NIK);
        when(repository.findById(NIK)).thenReturn(Optional.of(entity));
        when(repository.save(any(Biodata.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = service.patchBiodata(NIK, new BiodataPatchRequest(), true);

        assertEquals(NIK, result);
        assertTrue(entity.getChangedStatus(), "self-service harus changedStatus=true");
        verify(profileUpdateService).create(NIK, RevisionMetadata.RevisionType.UPDATE, EProfileUpdateTable.BIODATA);
    }

    @Test
    void patchBiodata_requiresApprovalFalse_staysStable() {
        Biodata entity = new Biodata(NIK);
        when(repository.findById(NIK)).thenReturn(Optional.of(entity));
        when(repository.save(any(Biodata.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = service.patchBiodata(NIK, new BiodataPatchRequest(), false);

        assertEquals(NIK, result);
        assertFalse(entity.getChangedStatus(), "admin edit harus changedStatus=false (langsung stable)");
        verify(profileUpdateService, never()).create(any(), any(), any());
    }
}
