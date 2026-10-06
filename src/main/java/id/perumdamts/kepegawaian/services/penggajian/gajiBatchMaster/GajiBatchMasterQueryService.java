package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiDto;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiKomponenItemDto;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterSelfQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchMasterProsesQueryRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchMasterQueryRepository;
import id.perumdamts.kepegawaian.utils.DownloadPenggajian;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiBatchMasterQueryService {
    private final GajiBatchMasterQueryRepository queryRepository;
    private final GajiBatchMasterProsesQueryRepository prosesRepository;
    private final DownloadPenggajian downloadPenggajian;

    public Page<GajiBatchMasterResponse> findPage(GajiBatchMasterIndexQuery query) {
        return queryRepository.pageQuery(query);
    }

    public List<GajiBatchMasterResponse> findAll(GajiBatchMasterIndexQuery query) {
        return queryRepository.listQuery(query);
    }

    public Optional<GajiBatchMasterResponse> findById(Long id) {
        return queryRepository.getById(id);
    }

    public Page<GajiBatchMasterResponse> findByPegawaiId(Long pegawaiId, GajiBatchMasterIndexQuery query) {
        return queryRepository.findByPegawaiId(pegawaiId, query);
    }

    public SlipGajiDto getSlipGaji(Long id) {
        GajiBatchMasterResponse master = queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Data penggajian tidak ditemukan"));

        Integer rootStatus = queryRepository.getBatchRootStatusByMasterId(id);
        if (rootStatus == null || rootStatus != EProsesGaji.FINISHED.ordinal()) {
            throw new BadRequestException("Slip gaji hanya dapat diunduh jika status batch sudah FINISHED");
        }

        List<GajiBatchMasterProsesResponse> prosesList = prosesRepository.findByMasterId(id);

        // 5 item standar penerimaan yang ditampilkan di slip gaji
        Double gajiPokok = findPemasukanNilai(prosesList, "GP", "gaji");
        Double tunjSi = findPemasukanNilai(prosesList, "TUNJ_SI", "suami", "istri");
        Double tunjAnak = findPemasukanNilai(prosesList, "TUNJ_ANAK", "anak");
        Double tunjJabatan = findPemasukanNilai(prosesList, "TUNJ_JABATAN", "jabatan");
        Double tunjKinerja = findPemasukanNilai(prosesList, "TUNJ_KINERJA", "kinerja", "kpi", "tunj_kk");

        List<SlipGajiKomponenItemDto> penerimaan = List.of(
                new SlipGajiKomponenItemDto("GP", "Gaji", gajiPokok),
                new SlipGajiKomponenItemDto("TUNJ_SI", "Tunjangan Suami Istri", tunjSi),
                new SlipGajiKomponenItemDto("TUNJ_ANAK", "Tunjangan Anak", tunjAnak),
                new SlipGajiKomponenItemDto("TUNJ_JABATAN", "Tunjangan Jabatan", tunjJabatan),
                new SlipGajiKomponenItemDto("TUNJ_KINERJA", "Tunjangan Kinerja", tunjKinerja)
        );

        List<SlipGajiKomponenItemDto> potongan = prosesList.stream()
                .filter(p -> p.jenisGaji() == EJenisGaji.POTONGAN && !isAdd(p))
                .map(p -> new SlipGajiKomponenItemDto(p.kode(), p.nama(), p.nilai() != null ? p.nilai() : 0.0))
                .toList();

        List<SlipGajiKomponenItemDto> penerimaanTambahan = prosesList.stream()
                .filter(p -> p.jenisGaji() == EJenisGaji.PEMASUKAN && isAdd(p))
                .map(p -> new SlipGajiKomponenItemDto(p.kode(), p.nama(), p.nilai() != null ? p.nilai() : 0.0))
                .toList();

        List<SlipGajiKomponenItemDto> potonganTambahan = prosesList.stream()
                .filter(p -> p.jenisGaji() == EJenisGaji.POTONGAN && isAdd(p))
                .map(p -> new SlipGajiKomponenItemDto(p.kode(), p.nama(), p.nilai() != null ? p.nilai() : 0.0))
                .toList();

        // Total penerimaan dihitung dari akumulasi seluruh komponen berjenis PEMASUKAN
        Double totalPenerimaan = prosesList.stream()
                .filter(p -> p.jenisGaji() == EJenisGaji.PEMASUKAN)
                .mapToDouble(p -> p.nilai() != null ? p.nilai() : 0.0)
                .sum();
        if (totalPenerimaan == 0.0 && master.penghasilanKotor() != null) {
            totalPenerimaan = master.penghasilanKotor();
        }
        Double totalPotongan = master.totalPotongan() != null ? master.totalPotongan() :
                potongan.stream().mapToDouble(SlipGajiKomponenItemDto::nilai).sum();
        Double selisihPenerimaanPotongan = master.penghasilanBersih() != null ? master.penghasilanBersih() : (totalPenerimaan - totalPotongan);
        Double pembulatan = master.pembulatan() != null ? master.pembulatan() : 0.0;
        Double subTotal = master.penghasilanBersihFinal() != null ? master.penghasilanBersihFinal() : (selisihPenerimaanPotongan + pembulatan);

        Double totalPenerimaanTambahan = master.totalAddTambahan() != null ? master.totalAddTambahan() :
                penerimaanTambahan.stream().mapToDouble(SlipGajiKomponenItemDto::nilai).sum();
        Double totalPotonganTambahan = master.totalAddPotongan() != null ? master.totalAddPotongan() :
                potonganTambahan.stream().mapToDouble(SlipGajiKomponenItemDto::nilai).sum();

        Double totalDibayarkan = master.penghasilanBersihFinal2() != null ? master.penghasilanBersihFinal2() :
                (master.penghasilanBersihFinal() != null ? master.penghasilanBersihFinal() :
                        (subTotal + totalPenerimaanTambahan - totalPotonganTambahan));

        return new SlipGajiDto(
                master.id(),
                master.gajiBatchRootId(),
                master.periode(),
                master.pegawaiId(),
                master.nipam(),
                master.nama(),
                master.namaJabatan(),
                master.golongan(),
                master.pangkat(),
                "- - -",
                penerimaan,
                potongan,
                totalPenerimaan,
                totalPotongan,
                selisihPenerimaanPotongan,
                pembulatan,
                subTotal,
                penerimaanTambahan,
                potonganTambahan,
                totalPenerimaanTambahan,
                totalPotonganTambahan,
                totalDibayarkan
        );
    }

    public ResponseEntity<?> downloadTableGaji(String rootBatchId) {
        try {
            ByteArrayResource byteArrayResource = downloadPenggajian.downloadTableGaji(rootBatchId);
            if (byteArrayResource == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"table_gaji_" + rootBatchId + ".xlsx\"")
                    .body(byteArrayResource);
        } catch (Exception e) {
            return ErrorResult.build(e.getMessage());
        }
    }

    public ResponseEntity<?> downloadPotonganGaji(String rootBatchId) {
        try {
            ByteArrayResource byteArrayResource = downloadPenggajian.downloadPotonganGaji(rootBatchId);
            if (byteArrayResource == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"potongan_gaji_" + rootBatchId + ".xlsx\"")
                    .body(byteArrayResource);
        } catch (Exception e) {
            return ErrorResult.build(e.getMessage());
        }
    }

    public Page<GajiBatchMasterResponse> findHistoryByPegawaiId(Long pegawaiId, GajiBatchMasterSelfQuery query) {
        return queryRepository.findHistoryByPegawaiId(pegawaiId, query);
    }

    private Double findPemasukanNilai(List<GajiBatchMasterProsesResponse> list, String kodeMatch, String... keywords) {
        if (list == null) return 0.0;
        return list.stream()
                .filter(p -> p.jenisGaji() == EJenisGaji.PEMASUKAN)
                .filter(p -> {
                    if (p.kode() != null && p.kode().equalsIgnoreCase(kodeMatch)) return true;
                    if (p.nama() != null) {
                        String lower = p.nama().toLowerCase();
                        for (String kw : keywords) {
                            if (lower.contains(kw)) return true;
                        }
                    }
                    return false;
                })
                .mapToDouble(p -> p.nilai() != null ? p.nilai() : 0.0)
                .findFirst()
                .orElse(0.0);
    }

    private boolean isAdd(GajiBatchMasterProsesResponse p) {
        if (p == null) {
            return false;
        }
        String kode = p.kode() != null ? p.kode().trim().toUpperCase() : "";
        String nama = p.nama() != null ? p.nama().trim().toUpperCase() : "";
        return kode.startsWith("ADD") || nama.startsWith("ADD");
    }
}
