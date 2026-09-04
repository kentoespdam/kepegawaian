package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.*;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiPreloadService {

    private final GajiKomponenRepository gajiKomponenRepository;
    private final GajiTunjanganRepository gajiTunjanganRepository;
    private final GajiParameterSettingRepository gajiParameterSettingRepository;
    private final GajiPendapatanNonPajakRepository gajiPendapatanNonPajakRepository;
    private final GajiPotonganTkkRepository gajiPotonganTkkRepository;
    private final PegawaiRepository pegawaiRepository;
    private final GajiBatchPotonganTkkRepository gajiBatchPotonganTkkRepository;
    private final GajiKpiRepository gajiKpiRepository;
    private final RiwayatSpRepository riwayatSpRepository;
    private final ObjectProvider<GajiPreloadService> selfProvider;

    private GajiPreloadService getSelf() {
        return selfProvider != null ? selfProvider.getIfAvailable(() -> this) : this;
    }

    // ==========================================
    // KATEGORI A: Cached (@Cacheable Redis)
    // ==========================================

    @Cacheable(value = "gaji-referensi", key = "'komponen'")
    public Map<Long, List<GajiKomponen>> fetchKomponenAllProfil() {
        List<GajiKomponen> all = gajiKomponenRepository.findByOrderByUrutAsc();
        Map<Long, List<GajiKomponen>> map = new HashMap<>();
        for (GajiKomponen k : all) {
            if (k.getProfilGaji() != null && k.getProfilGaji().getId() != null) {
                map.computeIfAbsent(k.getProfilGaji().getId(), id -> new ArrayList<>()).add(k);
            }
        }
        return map;
    }

    @Cacheable(value = "gaji-referensi", key = "'tunjangan'")
    public GajiPreloadContext.PreloadTunjanganData fetchTunjangan() {
        List<GajiTunjangan> all = gajiTunjanganRepository.findAll();
        Map<GajiPreloadContext.JenisLevelKey, Double> byLevel = new HashMap<>();
        Map<GajiPreloadContext.JenisGolonganKey, Double> byGolongan = new HashMap<>();
        for (GajiTunjangan t : all) {
            if (t.getJenisTunjangan() != null && t.getLevel() != null && t.getGolongan() == null) {
                byLevel.put(new GajiPreloadContext.JenisLevelKey(t.getJenisTunjangan(), t.getLevel().getId()), t.getNominal());
            } else if (t.getJenisTunjangan() != null && t.getGolongan() != null) {
                byGolongan.put(new GajiPreloadContext.JenisGolonganKey(t.getJenisTunjangan(), t.getGolongan().getId()), t.getNominal());
            }
        }
        return new GajiPreloadContext.PreloadTunjanganData(byLevel, byGolongan);
    }

    @Cacheable(value = "gaji-referensi", key = "'parameter'")
    public Map<String, Double> fetchParameterSettings() {
        List<GajiParameterSetting> all = gajiParameterSettingRepository.findAll();
        Map<String, Double> map = new HashMap<>();
        for (GajiParameterSetting s : all) {
            if (s.getKode() != null) {
                map.put(s.getKode(), s.getNominal());
            }
        }
        return map;
    }

    @Cacheable(value = "gaji-referensi", key = "'ptkp'")
    public Map<Long, Double> fetchPtkp() {
        List<GajiPendapatanNonPajak> all = gajiPendapatanNonPajakRepository.findAll();
        Map<Long, Double> map = new HashMap<>();
        for (GajiPendapatanNonPajak p : all) {
            if (p.getId() != null) {
                map.put(p.getId(), p.getNominal());
            }
        }
        return map;
    }

    @Cacheable(value = "gaji-referensi", key = "'potongan-tkk'")
    public GajiPreloadContext.PreloadPotonganTkkData fetchPotonganTkk() {
        List<GajiPotonganTkk> all = gajiPotonganTkkRepository.findAll();
        Map<GajiPreloadContext.StatusLevelKey, Double> byLevel = new HashMap<>();
        Map<GajiPreloadContext.StatusGolonganKey, Double> byGolongan = new HashMap<>();
        Map<EStatusPegawai, Double> flat = new HashMap<>();
        for (GajiPotonganTkk p : all) {
            if (p.getStatusPegawai() != null) {
                if (p.getLevel() != null && p.getGolongan() == null) {
                    byLevel.put(new GajiPreloadContext.StatusLevelKey(p.getStatusPegawai(), p.getLevel().getId()), p.getNominal());
                } else if (p.getGolongan() != null) {
                    byGolongan.put(new GajiPreloadContext.StatusGolonganKey(p.getStatusPegawai(), p.getGolongan().getId()), p.getNominal());
                } else if (p.getLevel() == null && p.getGolongan() == null) {
                    flat.put(p.getStatusPegawai(), p.getNominal());
                }
            }
        }
        return new GajiPreloadContext.PreloadPotonganTkkData(byLevel, byGolongan, flat);
    }

    // ==========================================
    // KATEGORI B: Live per batch (tidak di-cache)
    // ==========================================

    public Map<Long, Boolean> fetchIsAskes(Collection<Long> pegawaiIds) {
        if (pegawaiIds == null || pegawaiIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = pegawaiRepository.findIsAskesByIdIn(pegawaiIds);
        Map<Long, Boolean> map = new HashMap<>();
        for (Object[] row : rows) {
            Long id = (Long) row[0];
            Boolean askes = (Boolean) row[1];
            map.put(id, Boolean.TRUE.equals(askes));
        }
        return map;
    }

    public Map<Long, Double> fetchSewaRumdin(Collection<Long> pegawaiIds) {
        if (pegawaiIds == null || pegawaiIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = pegawaiRepository.findRumahDinasNilaiByIdIn(pegawaiIds);
        Map<Long, Double> map = new HashMap<>();
        for (Object[] row : rows) {
            Long id = (Long) row[0];
            Double nilai = (Double) row[1];
            map.put(id, nilai != null ? nilai : 0.0);
        }
        return map;
    }

    public Map<String, Double> fetchSumPotTkk(String batchId) {
        if (batchId == null || batchId.isBlank()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = gajiBatchPotonganTkkRepository.sumPotonganGroupByNipam(batchId);
        Map<String, Double> map = new HashMap<>();
        for (Object[] row : rows) {
            String nipam = (String) row[0];
            Double sum = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            map.put(nipam, sum);
        }
        return map;
    }

    public Map<String, Double> fetchKpi(String periode, Collection<String> nipams) {
        if (periode == null || nipams == null || nipams.isEmpty()) {
            return Collections.emptyMap();
        }
        String normalized = normalizePeriode(periode);
        List<GajiKpi> kpis = gajiKpiRepository.findByPeriodeAndNipamIn(normalized, nipams);
        Map<String, Double> map = new HashMap<>();
        for (GajiKpi k : kpis) {
            if (k.getNipam() != null) {
                map.put(k.getNipam(), k.getTunkin() != null ? k.getTunkin() : 0.0);
            }
        }
        return map;
    }

    public Set<Long> fetchSp3Aktif(Collection<Long> pegawaiIds, String periode) {
        if (pegawaiIds == null || pegawaiIds.isEmpty() || periode == null) {
            return Collections.emptySet();
        }
        try {
            YearMonth ym = YearMonth.parse(normalizePeriode(periode));
            LocalDate windowEnd = ym.atDay(20);
            LocalDate windowStart = ym.minusMonths(1).atDay(21);
            return riwayatSpRepository.findAllPegawaiIdsWithActiveSp3In(pegawaiIds, windowEnd, windowStart);
        } catch (Exception e) {
            log.warn("Periode tidak valid utk SP-3 preload: '{}'", periode);
            return Collections.emptySet();
        }
    }

    // ==========================================
    // Entry Point Preload
    // ==========================================

    public GajiPreloadContext preload(String batchId, String periode, List<GajiBatchMaster> masters) {
        GajiPreloadService proxy = getSelf();

        // Kategori A (Cached)
        Map<Long, List<GajiKomponen>> komponen = proxy.fetchKomponenAllProfil();
        Map<String, Double> parameter = proxy.fetchParameterSettings();
        GajiPreloadContext.PreloadPotonganTkkData potTkk = proxy.fetchPotonganTkk();
        GajiPreloadContext.PreloadTunjanganData tunjangan = proxy.fetchTunjangan();
        Map<Long, Double> ptkp = proxy.fetchPtkp();

        // Extract pegawaiIds & nipams
        Set<Long> pegawaiIds = new HashSet<>();
        Set<String> nipams = new HashSet<>();
        if (masters != null) {
            for (GajiBatchMaster m : masters) {
                if (m.getPegawaiId() != null) pegawaiIds.add(m.getPegawaiId());
                if (m.getNipam() != null) nipams.add(m.getNipam());
            }
        }

        // Kategori B (Live per batch)
        Map<Long, Boolean> isAskes = fetchIsAskes(pegawaiIds);
        Map<Long, Double> sewaRumdin = fetchSewaRumdin(pegawaiIds);
        Map<String, Double> sumPotTkk = fetchSumPotTkk(batchId);
        Map<String, Double> kpi = fetchKpi(periode, nipams);
        Set<Long> sp3 = fetchSp3Aktif(pegawaiIds, periode);

        return new GajiPreloadContext(
                komponen,
                parameter,
                potTkk.byStatusAndLevel(),
                potTkk.byStatusAndGolongan(),
                potTkk.flatByStatus(),
                tunjangan.byJenisAndLevel(),
                tunjangan.byJenisAndGolongan(),
                ptkp,
                isAskes,
                sewaRumdin,
                sumPotTkk,
                kpi,
                sp3
        );
    }

    private String normalizePeriode(String periode) {
        if (periode == null || periode.length() != 6 || !periode.chars().allMatch(Character::isDigit))
            return periode;
        return periode.substring(0, 4) + "-" + periode.substring(4);
    }
}
