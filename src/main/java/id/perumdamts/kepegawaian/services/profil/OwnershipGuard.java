package id.perumdamts.kepegawaian.services.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PelatihanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PengalamanKerjaRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ownership guard untuk jalur SELF profil (ADR-0038 + kepegawaian-3blf).
 *
 * <p>Resolve NIK milik principal saat ini (Appwrite {@code $id} = {@code pegawai.id} →
 * {@code biodata.nik}) dan pastikan NIK target (dari body request / entity) sama dengan
 * NIK tersebut. Gagal → {@link NotFoundException} (404) supaya tidak membocorkan
 * keberadaan data milik orang lain.</p>
 *
 * <p>Hanya dipanggil pada konteks SELF (requiresApproval=true). Endpoint admin tidak
 * menggunakan guard ini.</p>
 */
@Component
@RequiredArgsConstructor
public class OwnershipGuard {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PEGAWAI = "Unknown Pegawai";

    private final PegawaiRepository pegawaiRepository;
    private final PendidikanRepository pendidikanRepository;
    private final ProfilKeluargaRepository profilKeluargaRepository;
    private final KeahlianRepository keahlianRepository;
    private final PelatihanRepository pelatihanRepository;
    private final KartuIdentitasRepository kartuIdentitasRepository;
    private final PengalamanKerjaRepository pengalamanKerjaRepository;

    /**
     * NIK milik principal saat ini. DEV dan principal tanpa pegawai riil → 404
     * (self-service tanpa akun riil tidak terdefinisi).
     */
    public String selfNik() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppwriteUser user)) {
            throw new NotFoundException(UNKNOWN_BIODATA);
        }
        if ("DEV".equals(user.get$id())) {
            throw new NotFoundException("Self-service tidak tersedia untuk DEV — gunakan endpoint /admin/profil/...");
        }
        Pegawai pegawai = parsePegawaiId(user.get$id())
                .flatMap(pegawaiRepository::findById)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PEGAWAI));
        if (pegawai.getBiodata() == null) {
            throw new NotFoundException(UNKNOWN_BIODATA);
        }
        return pegawai.getBiodata().getNik();
    }

    /** Parse Appwrite {@code $id} → pegawai id; non-numerik → kosong (404, bukan 500). */
    private Optional<Long> parsePegawaiId(String $id) {
        try {
            return Optional.ofNullable($id).map(Long::valueOf);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Lempar 404 bila salah satu NIK target bukan milik principal. Variadic supaya
     * satu resolve {@link #selfNik()} cukup untuk beberapa target (mis. update:
     * pemilik entity + NIK baru dari request).
     */
    public void assertSelfOwns(String... targetNiks) {
        String selfNik = selfNik();
        for (String nik : targetNiks) {
            if (!selfNik.equals(nik)) {
                throw new NotFoundException(UNKNOWN_BIODATA);
            }
        }
    }

    /**
     * Lempar 404 bila lampiran (ref+refId) bukan milik principal. Resolve pemilik
     * lewat entity referensi; ref yang tidak dikenal (mis. FOTO_PROFIL lewat jalur
     * self) → 404.
     */
    public void assertSelfOwnsLampiran(EJenisLampiranProfil ref, Long refId) {
        assertSelfOwns(resolveLampiranOwner(ref, refId)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA)));
    }

    /**
     * NIK principal saat ini bila konteks READ adalah SELF; {@code null} bila konteks
     * admin (punya {@code PROFIL:READ} atau {@code ROLE_ADMIN}) — bebas baca semua.
     * Jalur read memakai endpoint yang sama untuk admin & self (beda dengan write yang
     * sudah di-split), jadi pembedanya principal, bukan endpoint.
     */
    public String readScopeNik() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppwriteUser user)) {
            throw new NotFoundException(UNKNOWN_BIODATA); // fail closed, konsisten dengan jalur write
        }
        // authorities = ROLE_* (dari prefs) + permission ENTITY:ACTION (inject JwtAuthFilter/
        // DevAuthFilter per request). Baca dari auth, bukan user.getAuthorities() yang cuma ROLE_.
        boolean adminScope = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "PROFIL:READ".equals(a.getAuthority()));
        return adminScope ? null : selfNik();
    }

    /**
     * Lempar 404 bila konteks read adalah SELF dan NIK bukan milik principal.
     * Admin (readScope {@code null}) tidak dibatasi.
     */
    public void assertSelfRead(String nik) {
        String selfNik = readScopeNik();
        if (selfNik != null && !selfNik.equals(nik)) {
            throw new NotFoundException(UNKNOWN_BIODATA);
        }
    }

    /**
     * Lempar 404 bila konteks read adalah SELF dan lampiran (ref+refId) bukan milik
     * principal. Admin tidak dibatasi.
     */
    public void assertSelfReadLampiran(EJenisLampiranProfil ref, Long refId) {
        String selfNik = readScopeNik();
        if (selfNik == null) {
            return;
        }
        String ownerNik = resolveLampiranOwner(ref, refId)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        if (!selfNik.equals(ownerNik)) {
            throw new NotFoundException(UNKNOWN_BIODATA);
        }
    }

    /** NIK pemilik dari entity referensi lampiran; kosong bila ref tak dikenal / tak ditemukan. */
    private Optional<String> resolveLampiranOwner(EJenisLampiranProfil ref, Long refId) {
        return switch (ref) {
            case PROFIL_KELUARGA -> profilKeluargaRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PENDIDIKAN -> pendidikanRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PELATIHAN -> pelatihanRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_KEAHLIAN -> keahlianRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case KARTU_IDENTITAS -> kartuIdentitasRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PENGALAMAN_KERJA -> pengalamanKerjaRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case FOTO_PROFIL -> Optional.empty(); // foto profil bukan entity — tidak lewat jalur lampiran self
        };
    }
}
