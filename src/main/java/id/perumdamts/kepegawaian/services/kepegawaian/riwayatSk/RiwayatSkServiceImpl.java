package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Deprecated
public class RiwayatSkServiceImpl implements RiwayatSkService {
    private final RiwayatSkCommandService commandService;
    private final RiwayatSkQueryService queryService;

    @Override
    public List<RiwayatSkResponse> findAll(RiwayatSkRequest request) {
        return queryService.findAll(request).stream()
                .map(q -> {
                    RiwayatSkResponse response = new RiwayatSkResponse();
                    response.setId(q.getId());
                    response.setNipam(q.getNipam());
                    response.setNama(q.getNama());
                    response.setNomorSk(q.getNomorSk());
                    response.setJenisSk(q.getJenisSk());
                    response.setTanggalSk(q.getTanggalSk());
                    response.setTmtBerlaku(q.getTmtBerlaku());
                    response.setGolongan(q.getGolongan());
                    response.setGajiPokok(q.getGajiPokok());
                    response.setMkgTahun(q.getMkgTahun());
                    response.setMkgBulan(q.getMkgBulan());
                    response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
                    response.setMkgbTahun(q.getMkgbTahun());
                    response.setMkgbBulan(q.getMkgbBulan());
                    response.setUpdateMaster(q.getUpdateMaster());
                    response.setNotes(q.getNotes());
                    return response;
                }).toList();
    }

    @Override
    public Page<RiwayatSkResponse> findPage(RiwayatSkRequest request) {
        return queryService.findPage(request)
                .map(q -> {
                    RiwayatSkResponse response = new RiwayatSkResponse();
                    response.setId(q.getId());
                    response.setNipam(q.getNipam());
                    response.setNama(q.getNama());
                    response.setNomorSk(q.getNomorSk());
                    response.setJenisSk(q.getJenisSk());
                    response.setTanggalSk(q.getTanggalSk());
                    response.setTmtBerlaku(q.getTmtBerlaku());
                    response.setGolongan(q.getGolongan());
                    response.setGajiPokok(q.getGajiPokok());
                    response.setMkgTahun(q.getMkgTahun());
                    response.setMkgBulan(q.getMkgBulan());
                    response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
                    response.setMkgbTahun(q.getMkgbTahun());
                    response.setMkgbBulan(q.getMkgbBulan());
                    response.setUpdateMaster(q.getUpdateMaster());
                    response.setNotes(q.getNotes());
                    return response;
                });
    }

    @Override
    public RiwayatSkResponse findById(Long id) {
        try {
            var q = queryService.findById(id);
            RiwayatSkResponse response = new RiwayatSkResponse();
            response.setId(q.getId());
            response.setNipam(q.getNipam());
            response.setNama(q.getNama());
            response.setNomorSk(q.getNomorSk());
            response.setJenisSk(q.getJenisSk());
            response.setTanggalSk(q.getTanggalSk());
            response.setTmtBerlaku(q.getTmtBerlaku());
            response.setGolongan(q.getGolongan());
            response.setGajiPokok(q.getGajiPokok());
            response.setMkgTahun(q.getMkgTahun());
            response.setMkgBulan(q.getMkgBulan());
            response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
            response.setMkgbTahun(q.getMkgbTahun());
            response.setMkgbBulan(q.getMkgbBulan());
            response.setUpdateMaster(q.getUpdateMaster());
            response.setNotes(q.getNotes());
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<RiwayatSkResponse> findByIds(List<Long> riwayatIds) {
        return queryService.findByIds(riwayatIds).stream()
                .map(q -> {
                    RiwayatSkResponse response = new RiwayatSkResponse();
                    response.setId(q.getId());
                    response.setNipam(q.getNipam());
                    response.setNama(q.getNama());
                    response.setNomorSk(q.getNomorSk());
                    response.setJenisSk(q.getJenisSk());
                    response.setTanggalSk(q.getTanggalSk());
                    response.setTmtBerlaku(q.getTmtBerlaku());
                    response.setGolongan(q.getGolongan());
                    response.setGajiPokok(q.getGajiPokok());
                    response.setMkgTahun(q.getMkgTahun());
                    response.setMkgBulan(q.getMkgBulan());
                    response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
                    response.setMkgbTahun(q.getMkgbTahun());
                    response.setMkgbBulan(q.getMkgbBulan());
                    response.setUpdateMaster(q.getUpdateMaster());
                    response.setNotes(q.getNotes());
                    return response;
                }).toList();
    }

    @Override
    public List<RiwayatSkResponse> findByPegawai(Long pegawaiId) {
        return queryService.findByPegawai(pegawaiId).stream()
                .map(q -> {
                    RiwayatSkResponse response = new RiwayatSkResponse();
                    response.setId(q.getId());
                    response.setNipam(q.getNipam());
                    response.setNama(q.getNama());
                    response.setNomorSk(q.getNomorSk());
                    response.setJenisSk(q.getJenisSk());
                    response.setTanggalSk(q.getTanggalSk());
                    response.setTmtBerlaku(q.getTmtBerlaku());
                    response.setGolongan(q.getGolongan());
                    response.setGajiPokok(q.getGajiPokok());
                    response.setMkgTahun(q.getMkgTahun());
                    response.setMkgBulan(q.getMkgBulan());
                    response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
                    response.setMkgbTahun(q.getMkgbTahun());
                    response.setMkgbBulan(q.getMkgbBulan());
                    response.setUpdateMaster(q.getUpdateMaster());
                    response.setNotes(q.getNotes());
                    return response;
                }).toList();
    }

    @Override
    public Page<RiwayatSkResponse> findByPegawaiId(Long pegawaiId, RiwayatSkRequest request) {
        return queryService.findByPegawaiId(pegawaiId, request)
                .map(q -> {
                    RiwayatSkResponse response = new RiwayatSkResponse();
                    response.setId(q.getId());
                    response.setNipam(q.getNipam());
                    response.setNama(q.getNama());
                    response.setNomorSk(q.getNomorSk());
                    response.setJenisSk(q.getJenisSk());
                    response.setTanggalSk(q.getTanggalSk());
                    response.setTmtBerlaku(q.getTmtBerlaku());
                    response.setGolongan(q.getGolongan());
                    response.setGajiPokok(q.getGajiPokok());
                    response.setMkgTahun(q.getMkgTahun());
                    response.setMkgBulan(q.getMkgBulan());
                    response.setKenaikanBerikutnya(q.getKenaikanBerikutnya());
                    response.setMkgbTahun(q.getMkgbTahun());
                    response.setMkgbBulan(q.getMkgbBulan());
                    response.setUpdateMaster(q.getUpdateMaster());
                    response.setNotes(q.getNotes());
                    return response;
                });
    }

    @Override
    public SavedStatus<?> save(RiwayatSkPostRequest request) {
        try {
            commandService.save(request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Riwayat SK Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public RiwayatSk saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        return commandService.createSkCapeg(request, pegawai);
    }

    @Override
    public RiwayatSk savePegawai(PegawaiPostRequest request, Pegawai pegawai) {
        return commandService.createSkPegawaiTetap(request, pegawai);
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatSkPutRequest request) {
        try {
            commandService.update(id, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Riwayat SK Updated");
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
}
