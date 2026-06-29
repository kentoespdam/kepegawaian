package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Deprecated
public class RiwayatSpServiceImpl implements RiwayatSpService {
    private final RiwayatSpCommandService commandService;
    private final RiwayatSpQueryService queryService;

    @Override
    public Page<RiwayatSpResponse> findPage(Long id, RiwayatSpRequest request) {
        return queryService.pageQuery(id, request)
                .map(this::toResponse);
    }

    @Override
    public RiwayatSpResponse findById(Long id) {
        try {
            var q = queryService.getById(id);
            return toResponse(q);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseEntity<?> getFile(Long id) {
        return queryService.getFile(id);
    }

    @Override
    public SavedStatus<?> save(RiwayatSpPostRequest request) {
        try {
            commandService.save(request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Riwayat SP Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, RiwayatSpPutRequest request) {
        try {
            commandService.update(id, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Riwayat SP Updated");
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

    private RiwayatSpResponse toResponse(id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpQuery q) {
        RiwayatSpResponse r = new RiwayatSpResponse();
        r.setId(q.getId());
        r.setPegawaiId(q.getPegawaiId());
        r.setNipam(q.getNipam());
        r.setNama(q.getNama());
        r.setOrganisasi(q.getOrganisasi());
        r.setNamaOrganisasi(q.getNamaOrganisasi());
        r.setJabatan(q.getJabatan());
        r.setNamaJabatan(q.getNamaJabatan());
        r.setNomorSp(q.getNomorSp());
        r.setTanggalSp(q.getTanggalSp());
        r.setJenisSp(q.getJenisSp());
        r.setSanksi(q.getSanksi());
        r.setSanksiNotes(q.getSanksiNotes());
        r.setTanggalEksekusiSanksi(q.getTanggalEksekusiSanksi());
        r.setTanggalMulai(q.getTanggalMulai());
        r.setTanggalSelesai(q.getTanggalSelesai());
        r.setPenandaTangan(q.getPenandaTangan());
        r.setJabatanPenandaTangan(q.getJabatanPenandaTangan());
        r.setFileName(q.getFileName());
        r.setMimeType(q.getMimeType());
        r.setNotes(q.getNotes());
        return r;
    }
}
