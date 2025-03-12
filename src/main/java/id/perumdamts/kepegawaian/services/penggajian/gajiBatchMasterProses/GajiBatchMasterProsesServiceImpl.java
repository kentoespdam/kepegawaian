package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchMasterProsesServiceImpl implements GajiBatchMasterProsesService {
    @Value("${penggajian.endpoint}")
    private String ENDPOINT;
    private final GajiBatchMasterProsesRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;

    @Override
    public Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiBatchMasterProsesResponse::from);
    }

    @Override
    public GajiBatchMasterProsesResponse findById(Long id) {
        return repository.findById(id).map(GajiBatchMasterProsesResponse::from).orElse(null);
    }

    @Override
    public List<GajiBatchMasterProsesResponse> findByMasterId(Long id) {
        Specification<GajiBatchMasterProses> where = Specification.where((root, query, cb) ->
                cb.equal(root.get("batchMasterId"), id));
        return repository.findAll(where).stream().map(GajiBatchMasterProsesResponse::from).toList();
    }

    @Override
    public SavedStatus<?> save(GajiBatchMasterProsesPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Komponen Gaji sudah ada");

        GajiBatchMaster gajiBatchMaster = gajiBatchMasterRepository.findById(request.getBatchMasterId())
                .orElseThrow(() -> new RuntimeException("Unknown Gaji Batch Master"));
        GajiBatchMasterProses entity = GajiBatchMasterProsesPostRequest.toEntity(request);
        repository.save(entity);

        recalculateAdditional(gajiBatchMaster);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity);
    }

    @Override
    public boolean rollback(String rootBatchId) {
        List<GajiBatchMaster> gbmList = gajiBatchMasterRepository.findByGajiBatchRoot_Id(rootBatchId);
        if (gbmList.isEmpty())
            return false;

        Specification<GajiBatchMasterProses> kodeSpec = (root, query, cb) ->
                cb.like(root.get("kode"), "ADD_%");
        Specification<GajiBatchMasterProses> gbmSpec = (root, query, cb) ->
                cb.in(root.get("batchMasterId")).value(gbmList.stream().map(GajiBatchMaster::getId).toList());
        Specification<GajiBatchMasterProses> where = kodeSpec.and(gbmSpec);
        List<GajiBatchMasterProses> gbpList = repository.findAll(where);
        if (!gbpList.isEmpty())
            repository.deleteAll(gbpList);

        List<GajiBatchMaster> list = gbmList.stream().peek(gbm -> {
            gbm.setTotalAddTambahan(0D);
            gbm.setTotalAddPotongan(0D);
            gbm.setPenghasilanBersih2(0D);
            gbm.setPembulatan2(0D);
            gbm.setPenghasilanBersihFinal2(0D);
        }).toList();
        gajiBatchMasterRepository.saveAll(list);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        Optional<GajiBatchMasterProses> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        Long batchMasterId = byId.get().getBatchMasterId();
        gajiBatchMasterRepository.findById(batchMasterId)
                .ifPresent(gbm -> {
                    gbm.setTotalAddTambahan(0D);
                    gbm.setTotalAddPotongan(0D);
                    gbm.setPenghasilanBersih2(0D);
                    gbm.setPembulatan2(0D);
                    gbm.setPenghasilanBersihFinal2(0D);
                    gajiBatchMasterRepository.save(gbm);
                });
        repository.deleteById(id);
        return true;
    }

    private void recalculateAdditional(GajiBatchMaster gajiBatchMaster) {
        List<GajiBatchMasterProses> gajiBatchMasterProsesList = repository.findByBatchMasterId(gajiBatchMaster.getId());
        double addPemasukan = getSumAdditionalByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.PEMASUKAN);
        double addPotongan = getSumAdditionalByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.POTONGAN);
        double totalPemasukan = getSumByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.PEMASUKAN);
        double totalPotongan = getSumByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.POTONGAN);

        double penghasilanBersih2 = totalPemasukan - totalPotongan;
        double pembulatan2 = Math.round((Math.ceil(penghasilanBersih2 / 100) * 100) - penghasilanBersih2);
        double penghasilanBersihFinal2 = penghasilanBersih2 - pembulatan2;

        gajiBatchMaster.setTotalAddTambahan(addPemasukan);
        gajiBatchMaster.setTotalAddPotongan(addPotongan);
        gajiBatchMaster.setPenghasilanBersih2(penghasilanBersih2);
        gajiBatchMaster.setPembulatan2(pembulatan2);
        gajiBatchMaster.setPenghasilanBersihFinal2(penghasilanBersihFinal2);
        gajiBatchMasterRepository.save(gajiBatchMaster);
    }

    private List<GajiBatchMasterProses> filterGajiBatchMasterProses(List<GajiBatchMasterProses> list, EJenisGaji jenisGaji) {
        return list.stream()
                .filter(gbr -> gbr.getJenisGaji().equals(jenisGaji))
                .toList();
    }

    private Double getSumByJenisGaji(List<GajiBatchMasterProses> list, EJenisGaji jenisGaji) {
        return filterGajiBatchMasterProses(list, jenisGaji)
                .stream()
                .mapToDouble(GajiBatchMasterProses::getNilai)
                .sum();
    }

    private Double getSumAdditionalByJenisGaji(List<GajiBatchMasterProses> list, EJenisGaji jenisGaji) {
        return filterGajiBatchMasterProses(list, jenisGaji)
                .stream().filter(gbr -> gbr.getKode().startsWith("ADD_"))
                .mapToDouble(GajiBatchMasterProses::getNilai)
                .sum();
    }
}
