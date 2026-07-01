package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaUpdateByCutiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprovalCutiCommand {
    private final RedisHelper redisHelper;
    private final CutiApprovalRepository repository;
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiApprovalChainRepository cutiApprovalChainRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiKuotaUpdateByCutiService cutiKuotaUpdateByCutiService;

    @Transactional
    public SavedStatus<?> savePengajuan(CutiApprovalPostRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }

        CutiPegawai leaveRequest = cutiPegawaiRepository
                .findByIdAndApprovalCutiStatusIn(request.getCutiId(), List.of(EApprovalCutiStatus.PENDING, EApprovalCutiStatus.RETURNED))
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

        Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("Unknown Approver Pegawai"));

        Long approverJabatanId = approver.getJabatan().getId();
        Long currentPicJabatanId = leaveRequest.getPicSaatIni().getId();
        if (!approverJabatanId.equals(currentPicJabatanId)) {
            throw new RuntimeException("You are not allowed to approve this leave request");
        }

        CutiApproval approvalEntity = CutiApprovalPostRequest.toEntity(request, leaveRequest, approver);

        switch (request.getApprovalStatus()) {
            case APPROVED, RETURNED -> doSaveAcceptReject(approvalEntity, leaveRequest);
            case REJECTED -> rejectCutiPegawai(approvalEntity, leaveRequest);
            default -> throw new RuntimeException("Unknown Approval Status");
        }

        return SavedStatus.build(ESaveStatus.SUCCESS, "Persetujuan Cuti Berhasil Disimpan");
    }

    private void doSaveAcceptReject(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository.findByRefCuti_Id(cutiPegawai.getId());

        CutiApprovalChain currentChain = approvalChains.stream()
                .filter(chain -> chain.getReadWriteStatus() == EReadWriteStatus.WRITE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Writable approval chain not found"));

        int currentLevel = currentChain.getApprovalLevel();
        int nextLevel = cutiApproval.getApprovalStatus().equals(EApprovalCutiStatus.APPROVED) ?
                currentLevel + 1 : currentLevel - 1;

        Optional<CutiApprovalChain> nextChainOpt = approvalChains.stream()
                .filter(chain -> chain.getApprovalLevel() == nextLevel)
                .findFirst();

        if (nextChainOpt.isPresent()) {
            advanceChainPointer(currentChain, nextChainOpt.get(), cutiPegawai, cutiApproval.getApprovalStatus());
            repository.save(cutiApproval);
        } else {
            terminateChain(currentChain, cutiPegawai, cutiApproval.getApprovalStatus());
            repository.save(cutiApproval);
            cutiKuotaUpdateByCutiService.updateKuota(cutiPegawai);
        }
    }

    private void advanceChainPointer(CutiApprovalChain current, CutiApprovalChain next, CutiPegawai cuti, EApprovalCutiStatus status) {
        current.setApprovalStatus(status);
        current.setReadWriteStatus(EReadWriteStatus.READ);
        next.setReadWriteStatus(EReadWriteStatus.WRITE);
        cuti.setApprovalLevel(next.getApprovalLevel());
        cuti.setPicSaatIni(new Jabatan(next.getJabatanId()));
    }

    private void terminateChain(CutiApprovalChain current, CutiPegawai cuti, EApprovalCutiStatus status) {
        current.setApprovalStatus(status);
        current.setReadWriteStatus(EReadWriteStatus.READ);
        cuti.setApprovalCutiStatus(status);
    }

    private void rejectCutiPegawai(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
        cutiPegawai.setApprovalLevel(cutiApproval.getApprovalLevel());
        cutiPegawai.setPicSaatIni(new Jabatan(cutiApproval.getJabatan().getId()));

        repository.save(cutiApproval);
        cutiApprovalChainRepository.findByRefCutiIdAndJabatanId(cutiPegawai.getId(), cutiApproval.getJabatan().getId())
                .ifPresent(chain -> {
                    chain.setReadWriteStatus(EReadWriteStatus.READ);
                    chain.setApprovalStatus(EApprovalCutiStatus.REJECTED);
                });
    }
}
