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
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaUpdateByCutiService;
import id.perumdamts.kepegawaian.services.cuti.klaim.KlaimCutiCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final KlaimCutiCommand klaimCutiCommand;
    private final ApprovalCutiCommand approvalCutiCommand;


    /**
     * Retrieves a page of approval details for the given cuti pegawai.
     *
     * @param cutiId  the id of the cuti pegawai
     * @param request the page request and specification
     * @return the page of approval mini responses
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
            return approvalCutiCommand.savePengajuan(request);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> saveKlaim(CutiApprovalPostRequest request) {
        try {
            return klaimCutiCommand.saveKlaim(request);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
