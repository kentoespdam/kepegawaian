package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Wave 5 — snapshot fase 1 engine gaji (keputusan #6): query Pegawai eligible
 * (keputusan #8), buat {@link GajiBatchMaster} per pegawai dgn snapshot lengkap,
 * simpan batch. Kalkulasi (Wave 6) kemudian membaca snapshot ini, bukan master data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesSnapshotService {

    private final PegawaiRepository pegawaiRepository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;

    @Transactional
    public List<GajiBatchMaster> snapshot(GajiBatchRoot batchRoot) {
        List<Pegawai> pegawaiList = pegawaiRepository.findEligibleForGaji(
                EStatusKerja.KARYAWAN_AKTIF, EStatusPegawai.NON_PEGAWAI);
        log.info("Snapshot gaji batch {}: {} pegawai eligible", batchRoot.getId(), pegawaiList.size());

        List<GajiBatchMaster> masters = pegawaiList.stream()
                .map(pegawai -> toMaster(batchRoot, pegawai))
                .toList();
        return gajiBatchMasterRepository.saveAll(masters);
    }

    private GajiBatchMaster toMaster(GajiBatchRoot batchRoot, Pegawai pegawai) {
        GajiBatchMaster master = new GajiBatchMaster();
        master.setGajiBatchRoot(batchRoot);
        master.setPeriode(batchRoot.getPeriode());
        master.setPegawaiId(pegawai.getId());
        master.setNipam(pegawai.getNipam());
        master.setNama(pegawai.getBiodata() != null ? pegawai.getBiodata().getNama() : null);
        master.setStatusPegawai(pegawai.getStatusPegawai());
        master.setGajiPokok(pegawai.getGajiPokok());
        master.setPhdp(pegawai.getPhdp());
        master.setStatusKawin(pegawai.getBiodata() != null ? pegawai.getBiodata().getStatusKawin() : null);
        master.setJmlTanggungan(pegawai.getJmlTanggungan());
        master.setJmlJiwa(jmlJiwa(pegawai));

        Jabatan jabatan = pegawai.getJabatan();
        if (jabatan != null) {
            master.setJabatanId(jabatan.getId());
            master.setNamaJabatan(jabatan.getNama());
            Level level = jabatan.getLevel();
            if (level != null)
                master.setLevelId(level.getId());
        }
        if (pegawai.getOrganisasi() != null) {
            master.setOrganisasi(pegawai.getOrganisasi());
            master.setNamaOrganisasi(pegawai.getOrganisasi().getNama());
        }
        if (pegawai.getGolongan() != null) {
            master.setGolonganId(pegawai.getGolongan().getId());
            master.setGolongan(pegawai.getGolongan().getGolongan());
            master.setPangkat(pegawai.getGolongan().getPangkat());
        }
        if (pegawai.getGajiProfil() != null)
            master.setGajiProfilId(pegawai.getGajiProfil().getId());
        if (pegawai.getKodePajak() != null) {
            master.setGajiPendapatanNonPajakId(pegawai.getKodePajak());
            master.setKodePajak(pegawai.getKodePajak().getKode());
        }
        return master;
    }

    /** 1 + JML_ANAK + (KAWIN/MENIKAH_SEKANTOR ? 1 : 0) — sama dgn resolver JML_JIWA. */
    private int jmlJiwa(Pegawai pegawai) {
        EStatusKawin kawin = pegawai.getBiodata() != null ? pegawai.getBiodata().getStatusKawin() : null;
        boolean menikah = kawin == EStatusKawin.KAWIN || kawin == EStatusKawin.MENIKAH_SEKANTOR;
        return 1 + (pegawai.getJmlTanggungan() != null ? pegawai.getJmlTanggungan() : 0) + (menikah ? 1 : 0);
    }
}