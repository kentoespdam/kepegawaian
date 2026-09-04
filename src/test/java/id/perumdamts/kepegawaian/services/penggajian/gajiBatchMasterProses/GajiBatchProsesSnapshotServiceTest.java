package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test {@link GajiBatchProsesSnapshotService} — mock repositori, entitas
 * nyata (Pegawai/Biodata/Jabatan/dll). Wave 5-1.
 */
@ExtendWith(MockitoExtension.class)
class GajiBatchProsesSnapshotServiceTest {

    private static final String BATCH_ID = "202509-001";

    @Mock
    private PegawaiRepository pegawaiRepository;
    @Mock
    private GajiBatchMasterRepository gajiBatchMasterRepository;

    private GajiBatchProsesSnapshotService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchProsesSnapshotService(pegawaiRepository, gajiBatchMasterRepository);
    }

    private GajiBatchRoot batch() {
        GajiBatchRoot root = new GajiBatchRoot();
        root.setId(BATCH_ID);
        root.setPeriode("202509");
        return root;
    }

    private Pegawai pegawai(String nipam, EStatusKawin kawin, Integer jmlTanggungan) {
        Biodata biodata = new Biodata();
        biodata.setNik("3171-" + nipam);
        biodata.setNama("Pegawai " + nipam);
        biodata.setStatusKawin(kawin);

        Level level = new Level(5L);
        Jabatan jabatan = new Jabatan(10L);
        jabatan.setNama("Staff");
        jabatan.setLevel(level);

        Organisasi organisasi = new Organisasi();
        organisasi.setId(3L);
        organisasi.setNama("Organisasi Test");

        Golongan golongan = new Golongan(2L);
        golongan.setGolongan("III/a");
        golongan.setPangkat("Penata Muda");

        GajiProfil gajiProfil = new GajiProfil(1L);
        GajiPendapatanNonPajak kodePajak = new GajiPendapatanNonPajak(1L, "K0", 54_000_000.0, null);

        Pegawai pegawai = new Pegawai();
        pegawai.setId(99L);
        pegawai.setNipam(nipam);
        pegawai.setBiodata(biodata);
        pegawai.setJabatan(jabatan);
        pegawai.setOrganisasi(organisasi);
        pegawai.setGolongan(golongan);
        pegawai.setGajiProfil(gajiProfil);
        pegawai.setKodePajak(kodePajak);
        pegawai.setStatusPegawai(EStatusPegawai.PEGAWAI);
        pegawai.setGajiPokok(5_000_000.0);
        pegawai.setPhdp(500_000.0);
        pegawai.setJmlTanggungan(jmlTanggungan);
        return pegawai;
    }

    @Test
    void snapshot_queryEligible_danSaveAll() {
        Pegawai aktif = pegawai("IT-01", EStatusKawin.KAWIN, 2);
        Pegawai nonPegawai = pegawai("IT-02", EStatusKawin.BELUM_KAWIN, 0);
        nonPegawai.setStatusPegawai(EStatusPegawai.NON_PEGAWAI);

        when(pegawaiRepository.findEligibleForGaji(EStatusKerja.KARYAWAN_AKTIF, EStatusPegawai.NON_PEGAWAI))
                .thenReturn(List.of(aktif, nonPegawai));
        when(gajiBatchMasterRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<GajiBatchMaster> saved = service.snapshot(batch());

        verify(pegawaiRepository).findEligibleForGaji(EStatusKerja.KARYAWAN_AKTIF, EStatusPegawai.NON_PEGAWAI);
        verify(gajiBatchMasterRepository).saveAll(argThat(list -> ((List<?>) list).size() == 2));
        assertEquals(2, saved.size());
    }

    @Test
    void snapshot_mappingFieldLengkap() {
        Pegawai pegawai = pegawai("IT-01", EStatusKawin.KAWIN, 2);
        when(pegawaiRepository.findEligibleForGaji(EStatusKerja.KARYAWAN_AKTIF, EStatusPegawai.NON_PEGAWAI))
                .thenReturn(List.of(pegawai));
        when(gajiBatchMasterRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        GajiBatchMaster master = service.snapshot(batch()).getFirst();

        assertEquals(BATCH_ID, master.getGajiBatchRoot().getId());
        assertEquals("202509", master.getPeriode());
        assertEquals(99L, master.getPegawaiId());
        assertEquals("IT-01", master.getNipam());
        assertEquals("Pegawai IT-01", master.getNama());
        assertEquals(EStatusPegawai.PEGAWAI, master.getStatusPegawai());
        assertEquals(5_000_000.0, master.getGajiPokok());
        assertEquals(500_000.0, master.getPhdp());
        assertEquals(EStatusKawin.KAWIN, master.getStatusKawin());
        assertEquals(2, master.getJmlTanggungan());
        // jabatan + level
        assertEquals(10L, master.getJabatanId());
        assertEquals("Staff", master.getNamaJabatan());
        assertEquals(5L, master.getLevelId());
        // organisasi
        assertEquals(3L, master.getOrganisasi().getId());
        assertEquals("Organisasi Test", master.getNamaOrganisasi());
        // golongan
        assertEquals(2L, master.getGolonganId());
        assertEquals("III/a", master.getGolongan());
        assertEquals("Penata Muda", master.getPangkat());
        // gajiProfil + kodePajak
        assertEquals(1L, master.getGajiProfilId());
        assertEquals(1L, master.getGajiPendapatanNonPajakId().getId());
        assertEquals("K0", master.getKodePajak());
        // jmlJiwa: 1 + 2 + 1 (KAWIN)
        assertEquals(4, master.getJmlJiwa());
    }

    @Test
    void snapshot_jmlJiwa_belumKawin_danNullRelasi() {
        Pegawai minimal = pegawai("IT-03", EStatusKawin.BELUM_KAWIN, 0);
        minimal.setJabatan(null);
        minimal.setOrganisasi(null);
        minimal.setGolongan(null);
        minimal.setGajiProfil(null);
        minimal.setKodePajak(null);
        when(pegawaiRepository.findEligibleForGaji(EStatusKerja.KARYAWAN_AKTIF, EStatusPegawai.NON_PEGAWAI))
                .thenReturn(List.of(minimal));
        when(gajiBatchMasterRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        GajiBatchMaster master = service.snapshot(batch()).getFirst();

        assertEquals(1, master.getJmlJiwa());
        assertNull(master.getJabatanId());
        assertNull(master.getNamaJabatan());
        assertNull(master.getLevelId());
        assertNull(master.getOrganisasi());
        assertNull(master.getGolonganId());
        assertNull(master.getGajiProfilId());
        assertNull(master.getGajiPendapatanNonPajakId());
        assertNull(master.getKodePajak());
    }
}