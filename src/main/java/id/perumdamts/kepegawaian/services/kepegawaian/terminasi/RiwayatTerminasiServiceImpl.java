package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Deprecated
public class RiwayatTerminasiServiceImpl implements RiwayatTerminasiService {
    private final RiwayatTerminasiCommandService commandService;
    private final RiwayatTerminasiQueryService queryService;
    private final PegawaiRepository pegawaiRepository;

    @Override
    public Page<RiwayatTerminasiResponse> findPage(RiwayatTerminasiRequest request) {
        return queryService.findPage(request).map(this::toResponse);
    }

    @Override
    public Page<PegawaiResponse> findPageCalonPensiun(RiwayatTerminasiRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusMonths(3);
        request.setTanggalTerminasi(end);
        request.setSortBy("Biodata.nama");
        request.setSortDirection("ASC");

        return pegawaiRepository.findAll(request.getCalonPensiunSpecification(), request.getPageable())
                .map(PegawaiResponse::from);
    }

    @Override
    public RiwayatTerminasiResponse findById(Long id) {
        try {
            var q = queryService.findById(id);
            return toResponse(q);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SavedStatus<?> save(RiwayatTerminasiPostRequest request) {
        try {
            commandService.save(request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Terminasi pegawai berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatTerminasiPutRequest request) {
        try {
            commandService.update(id, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Terminasi pegawai berhasil diupdate");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    private RiwayatTerminasiResponse toResponse(RiwayatTerminasiQuery q) {
        RiwayatTerminasiResponse r = new RiwayatTerminasiResponse();
        r.setId(q.getId());
        r.setAlasanTerminasi(q.getAlasanTerminasi());
        r.setPegawai(q.getPegawai());
        r.setNipam(q.getNipam());
        r.setNama(q.getNama());
        r.setNomorSk(q.getNomorSk());
        r.setOrganisasi(q.getOrganisasi());
        r.setNamaOrganisasi(q.getNamaOrganisasi());
        r.setJabatan(q.getJabatan());
        r.setNamaJabatan(q.getNamaJabatan());
        r.setGolongan(q.getGolongan());
        r.setNamaGolongan(q.getNamaGolongan());
        r.setTanggalTerminasi(q.getTanggalTerminasi());
        r.setTahunTerminasi(q.getTahunTerminasi());
        r.setMasaKerja(q.getMasaKerja());
        r.setNotes(q.getNotes());

        if (q.getSkTerminasi() != null) {
            RiwayatSkResponse sk = new RiwayatSkResponse();
            sk.setId(q.getSkTerminasi().getId());
            sk.setNipam(q.getSkTerminasi().getNipam());
            sk.setNama(q.getSkTerminasi().getNama());
            sk.setNomorSk(q.getSkTerminasi().getNomorSk());
            sk.setJenisSk(q.getSkTerminasi().getJenisSk());
            sk.setTanggalSk(q.getSkTerminasi().getTanggalSk());
            sk.setTmtBerlaku(q.getSkTerminasi().getTmtBerlaku());
            sk.setGolongan(q.getSkTerminasi().getGolongan());
            sk.setGajiPokok(q.getSkTerminasi().getGajiPokok());
            sk.setMkgTahun(q.getSkTerminasi().getMkgTahun());
            sk.setMkgBulan(q.getSkTerminasi().getMkgBulan());
            sk.setKenaikanBerikutnya(q.getSkTerminasi().getKenaikanBerikutnya());
            sk.setMkgbTahun(q.getSkTerminasi().getMkgbTahun());
            sk.setMkgbBulan(q.getSkTerminasi().getMkgbBulan());
            sk.setUpdateMaster(q.getSkTerminasi().getUpdateMaster());
            sk.setNotes(q.getSkTerminasi().getNotes());
            r.setSkTerminasi(sk);
        }

        if (q.getLampiranSkTerminasi() != null) {
            LampiranSkResponse lam = new LampiranSkResponse();
            lam.setId(q.getLampiranSkTerminasi().getId());
            lam.setRef(q.getLampiranSkTerminasi().getRef());
            lam.setRefId(q.getLampiranSkTerminasi().getRefId());
            lam.setFileName(q.getLampiranSkTerminasi().getFileName());
            lam.setMimeType(q.getLampiranSkTerminasi().getMimeType());
            lam.setNotes(q.getLampiranSkTerminasi().getNotes());
            lam.setDisetujui(q.getLampiranSkTerminasi().getDisetujui());
            lam.setDisetujuiOleh(q.getLampiranSkTerminasi().getDisetujuiOleh());
            lam.setTanggalDisetujui(q.getLampiranSkTerminasi().getTanggalDisetujui());
            r.setLampiranSkTerminasi(lam);
        }

        return r;
    }
}
