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
        Optional<String> ownerNik = switch (ref) {
            case PROFIL_KELUARGA -> profilKeluargaRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PENDIDIKAN -> pendidikanRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PELATIHAN -> pelatihanRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_KEAHLIAN -> keahlianRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case KARTU_IDENTITAS -> kartuIdentitasRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case PROFIL_PENGALAMAN_KERJA -> pengalamanKerjaRepository.findById(refId).map(e -> e.getBiodata().getNik());
            case FOTO_PROFIL -> Optional.empty(); // foto profil bukan entity — tidak lewat jalur lampiran self
        };
        assertSelfOwns(ownerNik.orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA)));
    }
}
