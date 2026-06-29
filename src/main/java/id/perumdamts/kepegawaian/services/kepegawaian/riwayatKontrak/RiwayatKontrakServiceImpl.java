package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Deprecated
public class RiwayatKontrakServiceImpl implements RiwayatKontrakService {
    private final RiwayatKontrakCommandService commandService;
    private final RiwayatKontrakQueryService queryService;

    @Override
    public Page<RiwayatKontrakResponse> findByPegawaiId(Long id, RiwayatKontrakRequest request) {
        request.setPegawaiId(id);
        return queryService.findPage(request).map(this::toResponse);
    }

    @Override
    public RiwayatKontrakResponse findById(Long id) {
        try {
            var q = queryService.findById(id);
            return toResponse(q);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SavedStatus<?> save(RiwayatKontrakPostRequest request) {
        try {
            commandService.save(request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Kontrak Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatKontrakPutRequest request) {
        try {
            commandService.update(id, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Kontrak Updated");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            commandService.delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private RiwayatKontrakResponse toResponse(RiwayatKontrakQuery q) {
        RiwayatKontrakResponse r = new RiwayatKontrakResponse();
        r.setId(q.getId());
        r.setJenisKontrak(q.getJenisKontrak());
        r.setNipam(q.getNipam());
        r.setNama(q.getNama());
        r.setNomorKontrak(q.getNomorKontrak());
        r.setTanggalSk(q.getTanggalSk());
        r.setTanggalMulai(q.getTanggalMulai());
        r.setTanggalSelesai(q.getTanggalSelesai());
        r.setNotes(q.getNotes());
        return r;
    }
}
