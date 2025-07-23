package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaUpdateByCutiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CutiApprovalServiceImpl implements CutiApprovalService {
    private final RedisHelper redisHelper;
    private final CutiApprovalRepository repository;
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiApprovalChainRepository cutiApprovalChainRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiKuotaUpdateByCutiService cutiKuotaUpdateByCutiService;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdmId;

    @Value("${custom.jenisCuti.tahunan}")
    private Long jenisCutiTahunan;
    @Value("${custom.jenisCuti.ibadah}")
    private Long jenisCutiIbadah;

    @Override
    public Page<CutiApprovalMiniResponse> findPage(Long cutiId, CutiApprovalRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiApprovalMiniResponse::from);
    }


    /**
     * Saves the approval of a leave request.
     *
     * <p>This method checks if there is already a pending approval for the same leave request id.
     * If there is, an error will be thrown.</p>
     *
     * <p>It also checks if the approver pegawai exists.
     * If not, an error will be thrown.</p>
     *
     * <p>It also checks if the jabatan of the approver pegawai is the same as the jabatan of the request.
     * If not, an error will be thrown.</p>
     *
     * <p>After all validations are successful, the method will call either the acceptPengajuan, rejectCutiPegawai,
     * or returnPengajuan method depending on the request's approval status.</p>
     *
     * @param request The request to save the approval of a leave request.
     * @return The status of the saved approval.
     */
    @Override
    public SavedStatus<?> savePengajuan(CutiApprovalPostRequest request) {
        try {
            // Validate CSRF token
            if (redisHelper.validateToken(request.getCsrfToken())) {
                throw new RuntimeException("Duplicate request detected");
            }

            // Validate leave request existence and status
            CutiPegawai leaveRequest = cutiPegawaiRepository
                    .findByIdAndApprovalCutiStatusIn(request.getCutiId(), List.of(EApprovalCutiStatus.PENDING, EApprovalCutiStatus.RETURNED))
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

            // Validate approver existence
            Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                    .orElseThrow(() -> new RuntimeException("Unknown Approver Pegawai"));

            // Validate approver's jabatan
            Long approverJabatanId = approver.getJabatan().getId();
            Long currentPicJabatanId = leaveRequest.getPicSaatIni().getId();
            if (!approverJabatanId.equals(currentPicJabatanId)) {
                throw new RuntimeException("You are not allowed to approve this leave request");
            }

            // Create new cuti approval entity
            CutiApproval approvalEntity = CutiApprovalPostRequest.toEntity(request, leaveRequest, approver);

            // Handle approval status
            switch (request.getApprovalStatus()) {
                case APPROVED -> acceptPengajuan(approvalEntity, leaveRequest);
                case REJECTED -> rejectCutiPegawai(approvalEntity, leaveRequest);
//                case RETURNED -> returnPengajuan(approvalEntity, leaveRequest);
                default -> throw new RuntimeException("Unknown Approval Status");
            }

            // Return success status
            return SavedStatus.build(ESaveStatus.SUCCESS, "Persetujuan Cuti Berhasil Disimpan");
        } catch (Exception e) {
            // Return failure status with error message
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    private void acceptPengajuan(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> chains = cutiApprovalChainRepository.findByRefCuti_Id(cutiPegawai.getId());
        CutiApprovalChain currentChain = chains.stream()
                .filter(chain -> chain.getReadWriteStatus().equals(EReadWriteStatus.WRITE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You are not allowed to approve this leave request"));
        Integer currentLevel = currentChain.getApprovalLevel();
        Optional<CutiApprovalChain> nextChain = chains.stream()
                .filter(chain -> chain.getApprovalLevel().equals(currentLevel + 1))
                .findFirst();

        if (nextChain.isPresent()) {
            currentChain.setApprovalStatus(cutiApproval.getApprovalStatus());
            currentChain.setReadWriteStatus(EReadWriteStatus.READ);
            CutiApprovalChain nextApprovalChain = nextChain.get();
            nextApprovalChain.setReadWriteStatus(EReadWriteStatus.WRITE);
            cutiPegawai.setApprovalLevel(nextApprovalChain.getApprovalLevel());
            cutiPegawai.setPicSaatIni(new Jabatan(nextApprovalChain.getJabatanId()));
            repository.save(cutiApproval);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalChainRepository.save(currentChain);
            cutiApprovalChainRepository.save(nextApprovalChain);
        } else {
            currentChain.setReadWriteStatus(EReadWriteStatus.READ);
            currentChain.setApprovalStatus(cutiApproval.getApprovalStatus());
            cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
            repository.save(cutiApproval);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalChainRepository.save(currentChain);
            cutiKuotaUpdateByCutiService.updateKuota(cutiPegawai);
        }
    }

    /**
     * Process return cuti pegawai.
     * *
     * This method fetches the approval chain whose approval level is less than or equal to
     * the approval level of the cuti pegawai and sorts them in descending order.
     * *
     * Then this method checks if the jabatan of the pegawai who is returning the cuti
     * is in the approval chain. If not, then it throws an exception.
     * *
     * Finally, this method saves the changes to the database.
     *
     * @param cutiApproval Entity cuti approval that is being returned
     * @param cutiPegawai  Entity cuti pegawai that is being returned
     */
//    private void returnPengajuan(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
//        // fetch the approval chain whose approval level is less than or equal to
//        // the approval level of the cuti pegawai and sort them in descending order
//        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository
//                .findByRefCuti_IdAndApprovalLevelLessThanEqualAndSkipOrderByApprovalLevelDesc(cutiPegawai.getId(), cutiPegawai.getApprovalLevel(), true);
//
//        if (approvalChains.isEmpty()) {
//            throw new RuntimeException("You have no permission to return this request");
//        }
//
//        // get the current and next approval chain
//        CutiApprovalChain currentApprovalChain = approvalChains.get(0);
//        CutiApprovalChain nextApprovalChain = approvalChains.size() > 1 ? approvalChains.get(1) : currentApprovalChain;
//
//        // check if the jabatan of the pegawai who is returning the cuti is in the approval chain
//        if (!approvalChains.stream().map(CutiApprovalChain::getJabatanId).toList().contains(cutiApproval.getJabatan().getId())) {
//            throw new RuntimeException("You have no permission to return this request");
//        }
//
//        // set the approval level and jabatan of the cuti approval entity
//        cutiApproval.setApprovalLevel(currentApprovalChain.getApprovalLevel());
//        cutiApproval.setJabatan(new Jabatan(currentApprovalChain.getJabatanId()));
//
//        // if there is a next approval chain, then set the approval level of the cuti pegawai to the approval level of the next approval chain
//        if (nextApprovalChain != null) {
//            cutiPegawai.setApprovalLevel(nextApprovalChain.getApprovalLevel());
//            cutiPegawai.setPicSaatIni(new Jabatan(nextApprovalChain.getJabatanId()));
//        }
//        cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
//
//        // save the changes to the database
//        repository.save(cutiApproval);
//        cutiPegawaiRepository.save(cutiPegawai);
//    }

    /**
     * Rejects a cuti pegawai.
     * This method updates the status of the cuti pegawai to REJECTED. It also
     * updates the approval level and the current position in charge (pic saat ini)
     * to reflect the rejection.
     *
     * @param cutiApproval The cuti approval entity containing the rejection details.
     * @param cutiPegawai  The cuti pegawai entity that is being rejected.
     */
    private void rejectCutiPegawai(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        // Update the approval status of the leave request to the status from the rejection details
        cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());

        // Set the approval level to the level from the rejection details
        cutiPegawai.setApprovalLevel(cutiApproval.getApprovalLevel());

        // Update the current position in charge to the position of the approver who rejected
        cutiPegawai.setPicSaatIni(new Jabatan(cutiApproval.getJabatan().getId()));

        // Save the updated cuti approval and cuti pegawai entities to the database
        repository.save(cutiApproval);
        cutiPegawaiRepository.save(cutiPegawai);
    }
}
