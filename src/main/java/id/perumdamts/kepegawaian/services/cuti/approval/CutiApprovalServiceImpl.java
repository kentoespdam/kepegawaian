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

    /**
     * Retrieves a page of approval details for the given cuti pegawai.
     *
     * @param cutiId   the id of the cuti pegawai
     * @param request  the page request and specification
     * @return         the page of approval mini responses
     */
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
                case APPROVED, RETURNED -> doSaveAcceptReject(approvalEntity, leaveRequest);
                case REJECTED -> rejectCutiPegawai(approvalEntity, leaveRequest);
                default -> throw new RuntimeException("Unknown Approval Status");
            }

            // Return success status
            return SavedStatus.build(ESaveStatus.SUCCESS, "Persetujuan Cuti Berhasil Disimpan");
        } catch (Exception e) {
            // Return failure status with error message
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Processes the acceptance or rejection of a leave request.
     * <p>
     * Updates the current approval chain based on the approval status
     * of the given {@link CutiApproval}. Adjusts the read/write status and approval
     * level of the current and next chains (if applicable). If the leave request is
     * approved, it progresses to the next approval level. If rejected, it reverses
     * to the previous level. Saves the updated entities to their respective repositories.
     *
     * @param cutiApproval the {@link CutiApproval} entity containing the approval status
     * @param cutiPegawai  the {@link CutiPegawai} entity associated with the leave request
     * @throws RuntimeException if there is no current writable approval chain
     */
    private void doSaveAcceptReject(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        // Retrieve all approval chains associated with the leave request
        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository.findByRefCuti_Id(cutiPegawai.getId());

        // Find the current writable approval chain
        CutiApprovalChain currentChain = approvalChains.stream()
                .filter(chain -> chain.getReadWriteStatus() == EReadWriteStatus.WRITE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Writable approval chain not found"));

        // Determine the next approval level based on the approval status
        int currentLevel = currentChain.getApprovalLevel();
        int nextLevel = cutiApproval.getApprovalStatus() == EApprovalCutiStatus.APPROVED ? currentLevel - 1 : currentLevel + 1;

        // Attempt to find the next approval chain
        Optional<CutiApprovalChain> nextChainOpt = approvalChains.stream()
                .filter(chain -> chain.getApprovalLevel() == nextLevel)
                .findFirst();

        if (nextChainOpt.isPresent()) {
            // If a next chain exists, update the current chain and transition to the next chain
            currentChain.setApprovalStatus(cutiApproval.getApprovalStatus());
            currentChain.setReadWriteStatus(EReadWriteStatus.READ);

            CutiApprovalChain nextChain = nextChainOpt.get();
            nextChain.setReadWriteStatus(EReadWriteStatus.WRITE);
            cutiPegawai.setApprovalLevel(nextChain.getApprovalLevel());
            cutiPegawai.setPicSaatIni(new Jabatan(nextChain.getJabatanId()));

            // Save the updated entities
            repository.save(cutiApproval);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalChainRepository.save(currentChain);
            cutiApprovalChainRepository.save(nextChain);
        } else {
            // If no next chain exists, finalize the current chain and update the leave request status
            currentChain.setApprovalStatus(cutiApproval.getApprovalStatus());
            currentChain.setReadWriteStatus(EReadWriteStatus.READ);
            cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());

            // Save the updated entities and update leave quotas
            repository.save(cutiApproval);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalChainRepository.save(currentChain);
            cutiKuotaUpdateByCutiService.updateKuota(cutiPegawai);
        }
    }


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
        cutiApprovalChainRepository.findByRefCutiIdAndJabatanId(cutiPegawai.getId(), cutiApproval.getJabatan().getId())
                .ifPresent(chain -> {
                    chain.setReadWriteStatus(EReadWriteStatus.READ);
                    cutiApprovalChainRepository.save(chain);
                });
    }
}
