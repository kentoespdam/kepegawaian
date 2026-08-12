package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.history.RevisionMetadata;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * ADR-0036 §7: mesin generik mengelola flow — routing approve/reject per action type
 * tanpa menyentuh entity secara langsung.
 */
class ProfileUpdateApprovalHandlerTest {

    private ProfileUpdateStrategy strategy;
    private ProfileUpdateApprovalHandler handler;

    @BeforeEach
    void setUp() {
        strategy = mock(ProfileUpdateStrategy.class);
        org.mockito.Mockito.when(strategy.table()).thenReturn(EProfileUpdateTable.KEAHLIAN);
        handler = new ProfileUpdateApprovalHandler(List.of(strategy));
    }

    private ProfileUpdate pending(EProfileUpdateTable table, RevisionMetadata.RevisionType type) {
        return ProfileUpdate.builder()
                .revId("7")
                .tableName(table)
                .actionType(type)
                .build();
    }

    @Test
    void approvedRoutesToMarkAsStable() {
        handler.changeHandler(pending(EProfileUpdateTable.KEAHLIAN, RevisionMetadata.RevisionType.UPDATE),
                EProfileUpdateApproval.APPROVED);
        verify(strategy).markAsStable("7");
    }

    @Test
    void rejectedInsertRoutesToHandleRejectedInsert() {
        handler.changeHandler(pending(EProfileUpdateTable.KEAHLIAN, RevisionMetadata.RevisionType.INSERT),
                EProfileUpdateApproval.REJECT);
        verify(strategy).handleRejectedInsert("7");
    }

    @Test
    void rejectedUpdateRoutesToRevert() {
        ProfileUpdate pu = pending(EProfileUpdateTable.KEAHLIAN, RevisionMetadata.RevisionType.UPDATE);
        handler.changeHandler(pu, EProfileUpdateApproval.REJECT);
        verify(strategy).revertToPreviousRevision(pu);
    }

    @Test
    void rejectedDeleteRoutesToResetEntityState() {
        handler.changeHandler(pending(EProfileUpdateTable.KEAHLIAN, RevisionMetadata.RevisionType.DELETE),
                EProfileUpdateApproval.REJECT);
        verify(strategy).resetEntityState("7");
    }
}
