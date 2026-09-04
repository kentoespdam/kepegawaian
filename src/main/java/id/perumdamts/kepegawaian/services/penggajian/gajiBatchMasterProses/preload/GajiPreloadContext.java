package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable context berisi data referensi penggajian yang di-preload sebelum kalkulasi paralel.
 * Kategori A berasal dari Redis cache (atau DB saat cache miss).
 * Kategori B berasal dari live query per batch.
 */
public record GajiPreloadContext(
        // KATEGORI A — @Cacheable Redis
        Map<Long, List<GajiKomponen>> komponenByProfilId,
        Map<String, Double> parameterSettings,
        Map<StatusLevelKey, Double> potTkkByStatusAndLevel,
        Map<StatusGolonganKey, Double> potTkkByStatusAndGolongan,
        Map<EStatusPegawai, Double> potTkkFlatByStatus,
        Map<JenisLevelKey, Double> tunjanganByJenisAndLevel,
        Map<JenisGolonganKey, Double> tunjanganByJenisAndGolongan,
        Map<Long, Double> ptkpNominalById,

        // KATEGORI B — Live per batch (tidak di-cache)
        Map<Long, Boolean> isAskesByPegawaiId,
        Map<Long, Double> sewaRumdinByPegawaiId,
        Map<String, Double> sumPotonganTkkByNipam,
        Map<String, Double> kpiTunkinByNipam,
        Set<Long> pegawaiIdsWithActiveSp3
) implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(GajiPreloadContext.class);

    public record StatusLevelKey(EStatusPegawai statusPegawai, Long levelId) implements Serializable {}
    public record StatusGolonganKey(EStatusPegawai statusPegawai, Long golonganId) implements Serializable {}
    public record JenisLevelKey(EJenisTunjangan jenis, Long levelId) implements Serializable {}
    public record JenisGolonganKey(EJenisTunjangan jenis, Long golonganId) implements Serializable {}

    public record PreloadTunjanganData(
            Map<JenisLevelKey, Double> byJenisAndLevel,
            Map<JenisGolonganKey, Double> byJenisAndGolongan
    ) implements Serializable {}

    public record PreloadPotonganTkkData(
            Map<StatusLevelKey, Double> byStatusAndLevel,
            Map<StatusGolonganKey, Double> byStatusAndGolongan,
            Map<EStatusPegawai, Double> flatByStatus
    ) implements Serializable {}

    /**
     * Resolve satu kode komponen referensi (#SYSTEM) menjadi nilai double (in-memory lookup).
     */
    public double resolve(String kode, GajiBatchMaster master, Map<String, Double> ctx) {
        return switch (kode) {
            case "GP" -> nvl(master.getGajiPokok());
            case "JML_ANAK" -> nvl(master.getJmlTanggungan());
            case "JML_JIWA" -> jmlJiwa(master);
            case "REF_PTKP" -> resolvePtkp(master);
            case "REF_ASKES" -> resolveAskes(master);
            case "REF_SEWA_RUMDIN" -> resolveSewaRumdin(master);
            case "REF_POT_TKK" -> resolvePotTkk(master);
            case "REF_JML_POT_KK", "REF_JML_POT_TKK" -> resolveJmlPotTkk(master);
            case "REF_TUNJ_JABATAN" -> resolveTunjangan(master, EJenisTunjangan.JABATAN);
            case "REF_TUNJ_BERAS" -> resolveTunjangan(master, EJenisTunjangan.BERAS);
            case "REF_TUNJ_KK" -> resolveTunjangan(master, EJenisTunjangan.KINERJA);
            case "REF_TUNJ_AIR" -> resolveTunjangan(master, EJenisTunjangan.AIR);
            case "REF_PHDP" -> nvl(master.getPhdp());
            case "TUNJ_KINERJA" -> resolveTunjKinerja(master);
            default -> {
                log.warn("Kode reference tidak dikenal di preloaded resolver: '{}' (pegawai {})", kode, master.getNipam());
                yield 0.0;
            }
        };
    }

    public double resolve(String kode, GajiBatchMaster master, Map<String, Double> ctx, String batchId) {
        return resolve(kode, master, ctx);
    }

    public double jmlJiwa(GajiBatchMaster master) {
        if (master == null) return 1.0;
        EStatusKawin kawin = master.getStatusKawin();
        boolean menikah = kawin == EStatusKawin.KAWIN || kawin == EStatusKawin.MENIKAH_SEKANTOR;
        return 1 + nvl(master.getJmlTanggungan()) + (menikah ? 1 : 0);
    }

    public double resolvePtkp(GajiBatchMaster master) {
        if (master == null || master.getGajiPendapatanNonPajakId() == null) {
            return 0.0;
        }
        Long id = master.getGajiPendapatanNonPajakId().getId();
        if (id != null && ptkpNominalById != null && ptkpNominalById.containsKey(id)) {
            Double val = ptkpNominalById.get(id);
            if (val != null) return val;
        }
        return nvl(master.getGajiPendapatanNonPajakId().getNominal());
    }

    public double resolveAskes(GajiBatchMaster master) {
        if (master == null || master.getPegawaiId() == null || isAskesByPegawaiId == null) {
            return 0.0;
        }
        Boolean askes = isAskesByPegawaiId.get(master.getPegawaiId());
        return Boolean.TRUE.equals(askes) ? 1.0 : 0.0;
    }

    public double resolveSewaRumdin(GajiBatchMaster master) {
        if (master == null || master.getPegawaiId() == null || sewaRumdinByPegawaiId == null) {
            return 0.0;
        }
        Double nilai = sewaRumdinByPegawaiId.get(master.getPegawaiId());
        return nvl(nilai);
    }

    public double resolvePotTkk(GajiBatchMaster master) {
        if (master == null || master.getStatusPegawai() == null) {
            return 0.0;
        }
        if (master.getLevelId() != null && potTkkByStatusAndLevel != null) {
            Double val = potTkkByStatusAndLevel.get(new StatusLevelKey(master.getStatusPegawai(), master.getLevelId()));
            if (val != null) return val;
        }
        if (master.getGolonganId() != null && potTkkByStatusAndGolongan != null) {
            Double val = potTkkByStatusAndGolongan.get(new StatusGolonganKey(master.getStatusPegawai(), master.getGolonganId()));
            if (val != null) return val;
        }
        if (potTkkFlatByStatus != null) {
            Double val = potTkkFlatByStatus.get(master.getStatusPegawai());
            if (val != null) return val;
        }
        return 0.0;
    }

    public double resolveJmlPotTkk(GajiBatchMaster master) {
        if (master == null || master.getNipam() == null || sumPotonganTkkByNipam == null) {
            return 0.0;
        }
        Double sum = sumPotonganTkkByNipam.get(master.getNipam());
        return nvl(sum);
    }

    public double resolveTunjangan(GajiBatchMaster master, EJenisTunjangan jenis) {
        if (master == null || jenis == null) {
            return 0.0;
        }
        if (jenis == EJenisTunjangan.KINERJA && isSp3Aktif(master)) {
            return 0.0;
        }
        if (master.getLevelId() != null && tunjanganByJenisAndLevel != null) {
            Double val = tunjanganByJenisAndLevel.get(new JenisLevelKey(jenis, master.getLevelId()));
            if (val != null) return val;
        }
        if (master.getGolonganId() != null && tunjanganByJenisAndGolongan != null) {
            Double val = tunjanganByJenisAndGolongan.get(new JenisGolonganKey(jenis, master.getGolonganId()));
            if (val != null) return val;
        }
        return 0.0;
    }

    public boolean isSp3Aktif(GajiBatchMaster master) {
        if (master == null || master.getPegawaiId() == null || pegawaiIdsWithActiveSp3 == null) {
            return false;
        }
        return pegawaiIdsWithActiveSp3.contains(master.getPegawaiId());
    }

    public double resolveTunjKinerja(GajiBatchMaster master) {
        if (master == null || master.getNipam() == null || kpiTunkinByNipam == null) {
            return 0.0;
        }
        Double tunkin = kpiTunkinByNipam.get(master.getNipam());
        return nvl(tunkin);
    }

    public List<GajiKomponen> resolveKomponenByProfilId(Long profilId) {
        return getKomponenByProfilId(profilId);
    }

    public List<GajiKomponen> getKomponenByProfilId(Long profilId) {
        if (komponenByProfilId == null || profilId == null) {
            return List.of();
        }
        return komponenByProfilId.getOrDefault(profilId, List.of());
    }

    public double clampPotongan(String kode, double nilai) {
        String paramKode = switch (kode) {
            case "POT_JP" -> "maksimal_potongan_jpn";
            case "POT_ASKES" -> "maksimal_potongan_askes";
            default -> null;
        };
        if (paramKode == null) {
            return nilai;
        }
        Double cap = parameterSettings != null ? parameterSettings.get(paramKode) : null;
        if (cap == null) {
            log.warn("Parameter clamp '{}' tidak ditemukan di preloaded context — {} tanpa cap", paramKode, kode);
            return nilai;
        }
        return Math.min(nilai, cap);
    }

    public Double resolveParameterSetting(String kode) {
        return parameterSettings != null ? parameterSettings.get(kode) : null;
    }

    private static double nvl(Double v) {
        return v != null ? v : 0.0;
    }

    private static double nvl(Integer v) {
        return v != null ? v : 0.0;
    }
}
