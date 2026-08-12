package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Regression (bd kepegawaian-yu5j): REJECT antrian biodata 500 "Illegally attempted to
 * associate proxy [JenjangPendidikan#7] with two open sessions". Root cause: revert
 * menyalin relasi dari entity hasil findLatestRevision (session audit Envers) ke entity
 * managed di session utama. Fix: salin hanya id, re-attach via getReferenceById di
 * session saat ini.
 */
@ExtendWith(MockitoExtension.class)
class BiodataProfileUpdateStrategyTest {

    @Mock private RevInfoService revInfoService;
    @Mock private BiodataRepository repository;
    @Mock private JenjangPendidikanRepository jenjangPendidikanRepository;

    private BiodataProfileUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BiodataProfileUpdateStrategy(revInfoService, repository, jenjangPendidikanRepository);
    }

    @Test
    void rejectUpdateReattachesRelationInCurrentSession() {
        JenjangPendidikan auditJenjang = new JenjangPendidikan(7L);
        Biodata prev = new Biodata("NIK");
        prev.setNama("Bagus Sudrajat");
        prev.setAlamat("Pajerukan RT 01/01");
        prev.setTelp("123456789321");
        prev.setPendidikanTerakhir(auditJenjang);
        when(revInfoService.findLatestRevision(eq(Biodata.class), eq("NIK"))).thenReturn(List.of(prev));

        JenjangPendidikan currentSessionRef = new JenjangPendidikan(7L);
        when(jenjangPendidikanRepository.getReferenceById(7L)).thenReturn(currentSessionRef);

        Biodata entity = new Biodata("NIK");
        when(repository.findById("NIK")).thenReturn(Optional.of(entity));

        strategy.revertToPreviousRevision(ProfileUpdate.builder().revId("NIK").build());

        // relasi tidak boleh menyalin proxy/entity dari session audit
        assertNotSame(auditJenjang, entity.getPendidikanTerakhir());
        assertSame(currentSessionRef, entity.getPendidikanTerakhir());
        verify(jenjangPendidikanRepository).getReferenceById(7L);
        // skalar tetap dikembalikan dari revisi sebelumnya
        assertEquals("123456789321", entity.getTelp());
        assertEquals("Bagus Sudrajat", entity.getNama());
        assertEquals(Boolean.FALSE, entity.getChangedStatus());
        verify(repository).save(entity);
    }
}
