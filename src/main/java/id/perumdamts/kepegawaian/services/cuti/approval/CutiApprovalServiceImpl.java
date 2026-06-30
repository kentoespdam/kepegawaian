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
    private final CutiApproveKlaimCutiService cutiApproveKlaimCutiService;
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

    public SavedStatus<?> saveKlaim(CutiApprovalPostRequest request) {
        try {
            // Validate CSRF token
            if (redisHelper.validateToken(request.getCsrfToken())) {
                throw new RuntimeException("Duplicate request detected");
            }

            CutiPegawai cutiPegawai = cutiPegawaiRepository.findById(request.getCutiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
            Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                    .orElseThrow(() -> new RuntimeException("Approver Pegawai not found"));

            if (!cutiPegawai.getPicSaatIni().equals(approver.getJabatan())) {
                throw new RuntimeException("Approver Pegawai not found");
            }

            CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawai, approver);

            cutiPegawai.setApprovalCutiStatus(request.getApprovalStatus());
            if (!request.getApprovalStatus().equals(EApprovalCutiStatus.APPROVED)) {
                cutiPegawaiRepository.save(cutiPegawai);
            } else {
                int nowYear = LocalDate.now().getYear();
                LocalDate tanggalMulai = cutiPegawai.getTanggalMulai();
                LocalDate tanggalSelesai = cutiPegawai.getTanggalSelesai();
                int startYear = tanggalMulai.getYear();
                int endYear = tanggalSelesai.getYear();

                if (startYear > nowYear && endYear > nowYear) {
                    cutiApproveKlaimCutiService.forNextYear(cutiPegawai, entity);
                } else if (startYear == nowYear && endYear > startYear) {
                    cutiApproveKlaimCutiService.overlappingYear(cutiPegawai, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                    cutiApproveKlaimCutiService.between1JanAnd30Jun(cutiPegawai, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                    cutiApproveKlaimCutiService.between1JulAnd31Dec(cutiPegawai, entity);
                } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                    cutiApproveKlaimCutiService.between30JunAnd1Jul(cutiPegawai, entity);
                } else {
                    throw new RuntimeException("Invalid request");
                }
            }
            cutiApprovalChainRepository.findByRefCutiIdAndJabatanId(cutiPegawai.getId(), approver.getJabatan().getId())
                    .ifPresent(chain -> {
                        chain.setReadWriteStatus(EReadWriteStatus.READ);
                        chain.setApprovalStatus(request.getApprovalStatus());
                        cutiApprovalChainRepository.save(chain);
                    });

            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disetujui");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
