package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
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
}
