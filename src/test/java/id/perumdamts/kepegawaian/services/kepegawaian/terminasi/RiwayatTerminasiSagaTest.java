package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatKontrakRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatMutasiRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSkRepository;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatTerminasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlasanBerhentiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression untuk saga POST /kepegawaian/riwayat/terminasi (ADR-0021):
 * {@code RiwayatTerminasiCommandService.save} adalah orkestrator tipis yang
 * mendelegasikan tiap tulis ke CommandService pemilik — SK lewat
 * {@code RiwayatSkCommandService.save}, mutasi lewat
 * {@code RiwayatMutasiCommandService.createFromTerminasi}, kontrak lewat
 * {@code RiwayatKontrakCommandService.createForTerminasi} (flag isLatest diurus
 * di service kontrak, bukan disalin). Saga juga men-set status pegawai menjadi
 * BERHENTI_OR_KELUAR.
 *
 * <p>Mengunci perilaku: kalau delegasi di-inline lagi (mapper + repository
 * langsung di orkestrator), test ini tetap hijau selama datanya sama — yang
 * dijaga di sini adalah hasil akhir saga (SK/mutasi/kontrak/status pegawai).
 * Data ditulis via JPA dan di-rollback otomatis (test @Transactional).
 */
@SpringBootTest
@ActiveProfiles("development")
@Transactional
class RiwayatTerminasiSagaTest {

    @Autowired private RiwayatTerminasiCommandService commandService;
    @Autowired private RiwayatTerminasiRepository terminasiRepository;
    @Autowired private RiwayatSkRepository skRepository;
    @Autowired private RiwayatMutasiRepository mutasiRepository;
    @Autowired private RiwayatKontrakRepository kontrakRepository;
    @Autowired private PegawaiRepository pegawaiRepository;
    @Autowired private BiodataRepository biodataRepository;
    @Autowired private OrganisasiRepository organisasiRepository;
    @Autowired private JabatanRepository jabatanRepository;
    @Autowired private GolonganRepository golonganRepository;
    @Autowired private AlasanBerhentiRepository alasanBerhentiRepository;
    @Autowired private ProfesiRepository profesiRepository;

    private static final LocalDate TANGGAL_TERMINASI = LocalDate.of(2026, 6, 30);

    private int seq = 0;
    private Organisasi organisasi;
    private Jabatan jabatan;
    private Golongan golongan;
    private AlasanBerhenti alasanBerhenti;
    private Profesi profesi;

    @BeforeEach
    void seedMasters() {
        seq++;
        organisasi = organisasiRepository.saveAndFlush(new Organisasi("ORG-SAGA-" + seq));
        jabatan = jabatanRepository.saveAndFlush(new Jabatan("JABATAN-SAGA-" + seq));
        golongan = golonganRepository.saveAndFlush(new Golongan("IV/a", "Pembina"));
        alasanBerhenti = alasanBerhentiRepository.saveAndFlush(new AlasanBerhenti("Pensiun", null));
        Profesi p = new Profesi();
        p.setNama("PROFESI-SAGA-" + seq);
        profesi = profesiRepository.saveAndFlush(p);
    }

    private Pegawai seedPegawai(EStatusPegawai statusPegawai) {
        seq++;
        Biodata biodata = new Biodata("NIK-SAGA-" + seq);
        biodata.setNama("PEGAWAI SAGA " + seq);
        biodataRepository.saveAndFlush(biodata);

        Pegawai pegawai = new Pegawai();
        pegawai.setNipam("SAGA" + System.nanoTime() + "-" + seq);
        pegawai.setBiodata(biodata);
        pegawai.setStatusPegawai(statusPegawai); // kolom NOT NULL
        pegawai.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        pegawai.setOrganisasi(organisasi);
        pegawai.setJabatan(jabatan);
        pegawai.setGolongan(golongan);
        pegawai.setProfesi(profesi); // RiwayatMutasiMapper.toEntity(terminasi) membaca profesi (NPE kalau null)
        pegawai.setTmtKerja(LocalDate.of(2010, 1, 1)); // dipakai hitung masaKerja
        return pegawaiRepository.saveAndFlush(pegawai);
    }

    private RiwayatTerminasiPostRequest request(Pegawai pegawai) {
        RiwayatTerminasiPostRequest r = new RiwayatTerminasiPostRequest();
        r.setPegawaiId(pegawai.getId());
        r.setNomorSk("SK-TERM-SAGA-" + seq + "-" + System.nanoTime());
        r.setJenisSk(EJenisSk.SK_PENSIUN);
        r.setTanggalSk(TANGGAL_TERMINASI);
        r.setTmtBerlaku(TANGGAL_TERMINASI);
        r.setGolonganId(golongan.getId());
        r.setAlasanTerminasiId(alasanBerhenti.getId());
        r.setNipam(pegawai.getNipam());
        r.setNama(pegawai.getBiodata().getNama());
        r.setOrganisasiId(organisasi.getId());
        r.setJabatanId(jabatan.getId());
        r.setNotes("saga test");
        return r;
    }

    @Test
    void saveTerminasi_pegawaiTetap_menulisSKMutasiDanSetStatusBerhenti() {
        Pegawai pegawai = seedPegawai(EStatusPegawai.PEGAWAI);

        RiwayatTerminasi saved = commandService.save(request(pegawai));

        // Terminasi: snapshot label + tanggal/tahun/masaKerja
        assertEquals(pegawai.getNipam(), saved.getNipam());
        assertEquals("Pensiun", saved.getAlasanTerminasi().getNama());
        assertEquals(organisasi.getNama(), saved.getNamaOrganisasi());
        assertEquals(jabatan.getNama(), saved.getNamaJabatan());
        assertEquals("Pembina - IV/a", saved.getNamaGolongan());
        assertEquals(TANGGAL_TERMINASI, saved.getTanggalTerminasi());
        assertEquals(2026, saved.getTahunTerminasi());
        assertEquals(16, saved.getMasaKerja());

        // SK terminasi (SK_PENSIUN)
        RiwayatSk sk = skRepository.findById(saved.getSkTerminasi().getId()).orElseThrow();
        assertEquals(EJenisSk.SK_PENSIUN, sk.getJenisSk());
        assertEquals(saved.getNomorSk(), sk.getNomorSk());

        // Mutasi TERMINASI
        RiwayatMutasi mutasi = mutasiRepository.findAll().stream()
                .filter(m -> pegawai.getId().equals(m.getPegawai().getId()))
                .findFirst().orElseThrow();
        assertEquals(EJenisMutasi.TERMINASI, mutasi.getJenisMutasi());
        assertEquals(TANGGAL_TERMINASI, mutasi.getTmtBerlaku());

        // Lifecycle pegawai
        Pegawai reloaded = pegawaiRepository.findById(pegawai.getId()).orElseThrow();
        assertEquals(EStatusKerja.BERHENTI_OR_KELUAR, reloaded.getStatusKerja());

        // Pegawai non-kontrak → tidak ada kontrak terminasi
        List<RiwayatKontrak> kontraks = kontrakRepository.findAll().stream()
                .filter(k -> pegawai.getId().equals(k.getPegawai().getId()))
                .toList();
        assertTrue(kontraks.isEmpty());
    }

    @Test
    void saveTerminasi_pegawaiKontrak_jugaMenulisKontrakTerminasiDanMemperbaruiLatest() {
        Pegawai pegawai = seedPegawai(EStatusPegawai.KONTRAK);

        // Kontrak lama (isLatest=true) — setelah terminasi harus turun ke false
        RiwayatKontrak oldKontrak = new RiwayatKontrak();
        oldKontrak.setJenisKontrak(EJenisKontrak.PERPANJANGAN);
        oldKontrak.setPegawai(pegawai);
        oldKontrak.setNipam(pegawai.getNipam());
        oldKontrak.setNama(pegawai.getBiodata().getNama());
        oldKontrak.setNomorKontrak("KTRK-LAMA-SAGA-" + seq);
        oldKontrak.setTanggalSk(LocalDate.of(2024, 1, 1));
        oldKontrak.setTanggalMulai(LocalDate.of(2024, 1, 1));
        oldKontrak.setTanggalSelesai(LocalDate.of(2025, 1, 1));
        oldKontrak.setIsLatest(true);
        kontrakRepository.saveAndFlush(oldKontrak);

        RiwayatTerminasi saved = commandService.save(request(pegawai));

        List<RiwayatKontrak> kontraks = kontrakRepository.findAll().stream()
                .filter(k -> pegawai.getId().equals(k.getPegawai().getId()))
                .sorted(Comparator.comparing(RiwayatKontrak::getId))
                .toList();
        assertEquals(2, kontraks.size());
        RiwayatKontrak terminasiKontrak = kontraks.getLast();
        assertEquals(EJenisKontrak.TERMINASI, terminasiKontrak.getJenisKontrak());
        assertTrue(terminasiKontrak.getIsLatest(), "kontrak terminasi harus isLatest=true");
        assertFalse(kontraks.getFirst().getIsLatest(), "kontrak lama harus isLatest=false (updateLatest di service kontrak)");
        assertEquals(saved.getSkTerminasi().getId(), terminasiKontrak.getRiwayatSk().getId());

        // Lifecycle tetap jalan untuk pegawai kontrak
        Pegawai reloaded = pegawaiRepository.findById(pegawai.getId()).orElseThrow();
        assertEquals(EStatusKerja.BERHENTI_OR_KELUAR, reloaded.getStatusKerja());
    }

    @Test
    void saveTerminasi_duplikat_menolakDenganConflict() {
        Pegawai pegawai = seedPegawai(EStatusPegawai.PEGAWAI);
        RiwayatTerminasiPostRequest request = request(pegawai);
        commandService.save(request);

        assertTrue(terminasiRepository.exists(request.getTerminasiSpecification()),
                "terminasi kedua dengan (pegawai, nomorSk, tanggalSk) sama harus terdeteksi");
    }
}
