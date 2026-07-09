package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchMasterProsesCommandService {
    private final GajiBatchMasterProsesRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;

    @Transactional
    public SavedStatus<Long> save(GajiBatchMasterProsesPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) throw new ConflictException("Komponen Gaji sudah ada");

        GajiBatchMaster gajiBatchMaster = gajiBatchMasterRepository.findById(request.getBatchMasterId())
                .orElseThrow(() -> new NotFoundException("Gaji Batch Master not found"));
        GajiBatchMasterProses entity = GajiBatchMasterProsesMapper.toEntity(request);
        repository.save(entity);

        recalculateAdditional(gajiBatchMaster);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
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

    @Transactional
    public boolean delete(Long id) {
        Optional<GajiBatchMasterProses> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        Long batchMasterId = byId.get().getBatchMasterId();
        repository.deleteById(id);
        gajiBatchMasterRepository.findById(batchMasterId).ifPresent(this::recalculateAdditional);
        return true;
    }

    private void recalculateAdditional(GajiBatchMaster gajiBatchMaster) {
        List<GajiBatchMasterProses> gajiBatchMasterProsesList = repository.findByBatchMasterId(gajiBatchMaster.getId());
        double addPemasukan = getSumAdditionalByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.PEMASUKAN);
        double addPotongan = getSumAdditionalByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.POTONGAN);
        double totalPemasukan = getSumByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.PEMASUKAN);
        double totalPotongan = getSumByJenisGaji(gajiBatchMasterProsesList, EJenisGaji.POTONGAN);

        double penghasilanBersih2 = Math.round(totalPemasukan - totalPotongan);
        double pembulatan2 = Math.round((Math.ceil(penghasilanBersih2 / 100)) * 100 - penghasilanBersih2);
        double penghasilanBersihFinal2 = penghasilanBersih2 + pembulatan2;

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
        return list.stream().filter(gbr -> gbr.getJenisGaji().equals(jenisGaji))
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
