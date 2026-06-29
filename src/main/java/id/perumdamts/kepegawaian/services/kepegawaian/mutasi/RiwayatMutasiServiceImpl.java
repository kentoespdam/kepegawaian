package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiResponse;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Deprecated
public class RiwayatMutasiServiceImpl implements RiwayatMutasiService {
    private final RiwayatMutasiCommandService commandService;
    private final RiwayatMutasiQueryService queryService;

    @Override
    public Page<RiwayatMutasiResponse> findPage(RiwayatMutasiRequest request) {
        return queryService.findPage(request).map(this::toResponse);
    }

    @Override
    public RiwayatMutasiResponse findById(Long id) {
        try {
            var q = queryService.findById(id);
            return toResponse(q);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SavedStatus<?> save(RiwayatMutasiPostRequest request) {
        try {
            commandService.save(request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Mutasi Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatMutasiPutRequest request) {
        try {
            commandService.update(id, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Mutasi Updated");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            commandService.delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private RiwayatMutasiResponse toResponse(RiwayatMutasiQuery q) {
        RiwayatMutasiResponse r = new RiwayatMutasiResponse();
        r.setId(q.getId());
        r.setNipam(q.getNipam());
        r.setNama(q.getNama());
        r.setJenisMutasi(q.getJenisMutasi());
        r.setTmtBerlaku(q.getTmtBerlaku());
        r.setTanggalBerakhir(q.getTanggalBerakhir());
        r.setGolongan(q.getGolongan());
        r.setOrganisasi(q.getOrganisasi());
        r.setNamaOrganisasi(q.getNamaOrganisasi());
        r.setJabatan(q.getJabatan());
        r.setNamaJabatan(q.getNamaJabatan());
        r.setProfesi(q.getProfesi());
        r.setNamaProfesi(q.getNamaProfesi());
        r.setGolonganLama(q.getGolonganLama());
        r.setOrganisasiLama(q.getOrganisasiLama());
        r.setNamaOrganisasiLama(q.getNamaOrganisasiLama());
        r.setJabatanLama(q.getJabatanLama());
        r.setNamaJabatanLama(q.getNamaJabatanLama());
        r.setProfesiLama(q.getProfesiLama());
        r.setNamaProfesiLama(q.getNamaProfesiLama());
        r.setNotes(q.getNotes());

        if (q.getSkMutasi() != null) {
            RiwayatSkResponse sk = new RiwayatSkResponse();
            sk.setId(q.getSkMutasi().getId());
            sk.setNipam(q.getSkMutasi().getNipam());
            sk.setNama(q.getSkMutasi().getNama());
            sk.setNomorSk(q.getSkMutasi().getNomorSk());
            sk.setJenisSk(q.getSkMutasi().getJenisSk());
            sk.setTanggalSk(q.getSkMutasi().getTanggalSk());
            sk.setTmtBerlaku(q.getSkMutasi().getTmtBerlaku());
            sk.setGolongan(q.getSkMutasi().getGolongan());
            sk.setGajiPokok(q.getSkMutasi().getGajiPokok());
            sk.setMkgTahun(q.getSkMutasi().getMkgTahun());
            sk.setMkgBulan(q.getSkMutasi().getMkgBulan());
            sk.setKenaikanBerikutnya(q.getSkMutasi().getKenaikanBerikutnya());
            sk.setMkgbTahun(q.getSkMutasi().getMkgbTahun());
            sk.setMkgbBulan(q.getSkMutasi().getMkgbBulan());
            sk.setUpdateMaster(q.getSkMutasi().getUpdateMaster());
            sk.setNotes(q.getSkMutasi().getNotes());
            r.setSkMutasi(sk);
        }

        return r;
    }
}
