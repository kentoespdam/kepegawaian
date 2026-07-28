package id.perumdamts.kepegawaian.entities.commons;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangedStatusPlacementTest {

    private boolean hasDeclaredChangedStatus(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equals("changedStatus")) return true;
        }
        return false;
    }

    private boolean inheritsChangedStatus(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            if (hasDeclaredChangedStatus(current)) return true;
            current = current.getSuperclass();
        }
        return false;
    }

    @Test
    void changedStatusMustNotLiveOnSharedSuperclass() {
        assertFalse(hasDeclaredChangedStatus(IdsAbstract.class),
                "changedStatus on IdsAbstract leaks the column into every entity's SELECT");
    }

    @Test
    void approvalEntitiesDeclareChangedStatus() {
        assertTrue(hasDeclaredChangedStatus(Biodata.class));
        assertTrue(hasDeclaredChangedStatus(Keahlian.class));
        assertTrue(hasDeclaredChangedStatus(Pelatihan.class));
        assertTrue(hasDeclaredChangedStatus(PengalamanKerja.class));
        assertTrue(hasDeclaredChangedStatus(KartuIdentitas.class));
        assertTrue(hasDeclaredChangedStatus(Pendidikan.class));
        assertTrue(hasDeclaredChangedStatus(ProfilKeluarga.class));
    }

    @Test
    void nonApprovalEntitiesHaveNoChangedStatus() {
        assertFalse(inheritsChangedStatus(LampiranProfil.class),
                "LampiranProfil must not select changed_status — its table has no such column");
    }
}
