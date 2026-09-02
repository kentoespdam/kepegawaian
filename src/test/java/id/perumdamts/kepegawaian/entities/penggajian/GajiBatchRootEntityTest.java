package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test: GajiBatchRoot.status is INTEGER in DB.
 * The entity must use {@code @Enumerated(EnumType.ORDINAL)} so Hibernate
 * writes an integer ordinal instead of a String, which would fail on save.
 */
class GajiBatchRootEntityTest {

    @Test
    void statusField_usesOrdinalEnumType() throws NoSuchFieldException {
        Field statusField = GajiBatchRoot.class.getDeclaredField("status");
        Enumerated annotation = statusField.getAnnotation(Enumerated.class);

        assertNotNull(annotation, "@Enumerated annotation must be present on status field");
        assertEquals(EnumType.ORDINAL, annotation.value(),
                "status must use EnumType.ORDINAL to match INTEGER DB column");
    }

    @Test
    void statusField_typeIsEProsesGaji() throws NoSuchFieldException {
        Field statusField = GajiBatchRoot.class.getDeclaredField("status");
        assertEquals(EProsesGaji.class, statusField.getType(),
                "status field must be of type EProsesGaji");
    }

    @Test
    void allEProsesGajiOrdinals_areContiguous() {
        EProsesGaji[] values = EProsesGaji.values();
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].ordinal(),
                    "Enum ordinal for " + values[i].name() + " must be " + i);
        }
    }

    @Test
    void entityCanBeSetWithEachStatus() {
        GajiBatchRoot entity = new GajiBatchRoot();

        for (EProsesGaji status : EProsesGaji.values()) {
            entity.setStatus(status);
            assertEquals(status, entity.getStatus(),
                    "Entity must accept and return status: " + status.name());
        }
    }

    @Test
    void entityDefaultConstructor_hasNullStatus() {
        GajiBatchRoot entity = new GajiBatchRoot();
        assertNull(entity.getStatus(), "Default status should be null");
    }
}
