package id.perumdamts.kepegawaian.services.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PelatihanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PengalamanKerjaRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-3blf: ownership check self — resolve NIK principal, 404 saat target
 * bukan miliknya (hindari info leak), resolve pemilik lampiran via ref+refId.
 * Unit test tanpa DB (Mockito) + SecurityContextHolder mock.
 */
@ExtendWith(MockitoExtension.class)
class OwnershipGuardTest {
    private static final String SELF_NIK = "3273012345678901";

    @Mock private PegawaiRepository pegawaiRepository;
    @Mock private PendidikanRepository pendidikanRepository;
    @Mock private ProfilKeluargaRepository profilKeluargaRepository;
    @Mock private KeahlianRepository keahlianRepository;
    @Mock private PelatihanRepository pelatihanRepository;
    @Mock private KartuIdentitasRepository kartuIdentitasRepository;
    @Mock private PengalamanKerjaRepository pengalamanKerjaRepository;

    @InjectMocks private OwnershipGuard guard;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String $id) {
        AppwriteUser user = new AppwriteUser();
        user.set$id($id);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of()));
    }

    private void stubSelfPegawai() {
        Pegawai pegawai = new Pegawai(5L);
        pegawai.setBiodata(new Biodata(SELF_NIK));
        when(pegawaiRepository.findById(5L)).thenReturn(Optional.of(pegawai));
    }

    @Test
    void selfNik_resolvesFromPegawaiBiodata() {
        loginAs("5");
        stubSelfPegawai();

        assertEquals(SELF_NIK, guard.selfNik());
    }

    @Test
    void assertSelfOwns_passesWhenTargetIsSelf() {
        loginAs("5");
        stubSelfPegawai();

        guard.assertSelfOwns(SELF_NIK);
        guard.assertSelfOwns(SELF_NIK, SELF_NIK); // variadic: semua target harus self
    }

    @Test
    void assertSelfOwns_throws404OnForeignNik() {
        loginAs("5");
        stubSelfPegawai();

        assertThrows(NotFoundException.class, () -> guard.assertSelfOwns("1234567890123456"));
        assertThrows(NotFoundException.class,
                () -> guard.assertSelfOwns(SELF_NIK, "1234567890123456"), "satu target asing cukup untuk 404");
    }

    @Test
    void assertSelfOwns_devThrows404() {
        loginAs("DEV");

        assertThrows(NotFoundException.class, () -> guard.assertSelfOwns(SELF_NIK));
    }

    @Test
    void selfNik_nonNumericIdThrows404Not500() {
        loginAs("abc");

        assertThrows(NotFoundException.class, guard::selfNik);
    }

    @Test
    void selfNik_pegawaiWithoutBiodataThrows404() {
        loginAs("5");
        when(pegawaiRepository.findById(5L)).thenReturn(Optional.of(new Pegawai(5L)));

        assertThrows(NotFoundException.class, guard::selfNik);
    }

    @Test
    void assertSelfOwnsLampiran_passesWhenRefEntityBelongsToSelf() {
        loginAs("5");
        stubSelfPegawai();
        Pendidikan pendidikan = new Pendidikan();
        pendidikan.setBiodata(new Biodata(SELF_NIK));
        when(pendidikanRepository.findById(anyLong())).thenReturn(Optional.of(pendidikan));

        guard.assertSelfOwnsLampiran(EJenisLampiranProfil.PROFIL_PENDIDIKAN, 99L);
    }

    @Test
    void assertSelfOwnsLampiran_throws404WhenRefEntityBelongsToSomeoneElse() {
        loginAs("5");
        stubSelfPegawai();
        Pendidikan pendidikan = new Pendidikan();
        pendidikan.setBiodata(new Biodata("1234567890123456"));
        when(pendidikanRepository.findById(anyLong())).thenReturn(Optional.of(pendidikan));

        assertThrows(NotFoundException.class,
                () -> guard.assertSelfOwnsLampiran(EJenisLampiranProfil.PROFIL_PENDIDIKAN, 99L));
    }

    @Test
    void assertSelfOwnsLampiran_throws404OnUnknownRefOrMissingEntity() {
        loginAs("5");

        assertThrows(NotFoundException.class,
                () -> guard.assertSelfOwnsLampiran(EJenisLampiranProfil.FOTO_PROFIL, 99L), "FOTO_PROFIL tidak punya entity");
        assertThrows(NotFoundException.class,
                () -> guard.assertSelfOwnsLampiran(EJenisLampiranProfil.PROFIL_KEAHLIAN, 99L), "refId tak ditemukan");
    }
}
