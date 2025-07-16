package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
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
     * Menyimpan persetujuan cuti pegawai.
     * *
     * Method ini akan memeriksa apakah cuti pegawai dengan id yang sama dan status PENDING
     * sudah ada. Jika sudah ada maka akan terjadi error.
     * *
     * Method ini juga akan memeriksa apakah pegawai yang akan di setujui cutinya sudah ada.
     * Jika tidak ada maka akan terjadi error.
     * *
     * Method ini juga akan memeriksa apakah jabatan pegawai yang akan di setujui cutinya
     * sama dengan jabatan yang di request. Jika tidak sama maka akan terjadi error.
     * *
     * Setelah semua validasi berhasil maka akan dijalankan prosedur approve atau reject
     * tergantung dari request.
     *
     * @param request Request untuk menyimpan persetujuan cuti pegawai.
     * @return Status hasil dari simpanan persetujuan cuti pegawai.
     */
    @Override
    public SavedStatus<?> savePengajuan(CutiApprovalPostRequest request) {
        try {
            if (redisHelper.validateToken(request.getCsrfToken())) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
            }
            // cek cuti pegawai exists and status is pending
            CutiPegawai cutiPegawai = cutiPegawaiRepository
                    .findByIdAndApprovalCutiStatusIn(request.getCutiId(), List.of(EApprovalCutiStatus.PENDING, EApprovalCutiStatus.RETURNED))
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

            // cek approver is exists
            Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                    .orElseThrow(() -> new RuntimeException("Unknown Approver Pegawai"));

            // cek jabatan approver sama dengan jabatan request
            if (!approver.getJabatan().getId().equals(supervisorSdmId)
                    && !approver.getJabatan().getId().equals(cutiPegawai.getPicSaatIni().getId())
            ) {
                throw new RuntimeException("Permission Denied");
            }

            // create new cuti approval entity
            CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawai, approver);

            // if request is reject, then call rejectPengajuan method
            if (request.getApprovalStatus().equals(EApprovalCutiStatus.REJECTED)) {
                rejectPengajuan(entity, cutiPegawai);
            } else if (request.getApprovalStatus().equals(EApprovalCutiStatus.RETURNED)) {
                // if request is return, then call returnPengajuan method
                returnPengajuan(entity, cutiPegawai);
            } else {
                // if request is approve, then call acceptPengajuan method
                acceptPengajuan(entity, cutiPegawai);
            }

            // return success status
            return SavedStatus.build(ESaveStatus.SUCCESS, "Persetujuan Cuti Berhasil Disimpan");
        } catch (Exception e) {
            // return failed status with error message
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Proses approve cuti pegawai.
     * *
     * Method ini akan fetch approval chain yang lebih besar dari approval level cuti pegawai
     * dan sortir berdasarkan approval level ascending.
     * *
     * Lalu method ini akan cek apakah jabatan pegawai yang akan di approve cutinya
     * berada di dalam approval chain yang di fetch.
     * *
     * Jika tidak berada di dalam approval chain maka akan terjadi error.
     * *
     * Lalu method ini akan simpan perubahan ke dalam database.
     *
     * @param cutiApproval Entity cuti approval yang akan di approve
     * @param cutiPegawai  Entity cuti pegawai yang akan di approve
     */
    private void acceptPengajuan(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        // fetch approval chain yang lebih besar dari approval level cuti pegawai
        // dan sortir berdasarkan approval level ascending
        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository
                .findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(
                        cutiPegawai.getId(), cutiApproval.getApprovalLevel());

        // cek apakah approval chain memiliki lebih dari satu element
        // jika tidak maka berarti currentChain adalah element yang terakhir
        // dan tidak ada next approval
        CutiApprovalChain currentChain = approvalChains.getFirst();
        CutiApprovalChain nextChain = approvalChains.size() > 1 ? approvalChains.get(1) : null;

        // cek apakah approval level cuti approval lebih kecil dari approval level cuti pegawai
        // jika ya maka berarti tidak memiliki permission untuk approve request ini
        if (currentChain.getApprovalLevel() < cutiPegawai.getApprovalLevel()) {
            throw new RuntimeException("You have no permission to approve this request");
        }

        // cek apakah jabatan pegawai yang akan di approve cutinya
        // berada di dalam approval chain yang di fetch
        Long jabatanId = cutiApproval.getJabatan().getId();
        if (!approvalChains.stream().map(CutiApprovalChain::getJabatanId).toList().contains(jabatanId)) {
            throw new RuntimeException("You have no permission to approve this request");
        }

        // simpan perubahan ke dalam database
        saveReturnReject(cutiApproval, cutiPegawai, nextChain);
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
    private void returnPengajuan(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        // fetch approval chain
        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository
                .findByRefCutiIdAndApprovalLevelLessThanEqualOrderByApprovalLevelDesc(
                        cutiPegawai.getId(), cutiPegawai.getApprovalLevel());

        // check approver is in approval chain
        Long jabatanId = cutiApproval.getJabatan().getId();
        if (!approvalChains.stream().map(CutiApprovalChain::getJabatanId).toList().contains(jabatanId)) {
            throw new RuntimeException("You have no permission to approve this request");
        }

        CutiApprovalChain nextChain = approvalChains.size() > 1 ? approvalChains.get(1) : null;

        // save return or reject
        saveReturnReject(cutiApproval, cutiPegawai, nextChain);
    }

    /**
     * Process reject cuti pegawai.
     * *
     * This method will save the changes to the database.
     * *
     * This method will update the status of the cuti pegawai to REJECTED and
     * update the approval level to the approval level of the cuti approval that
     * is being rejected and update the pic saat ini to the jabatan of the cuti
     * approval that is being rejected.
     * *
     *
     * @param cutiApproval Entity cuti approval that is being rejected
     * @param cutiPegawai  Entity cuti pegawai that is being rejected
     */
    private void rejectPengajuan(CutiApproval cutiApproval, CutiPegawai cutiPegawai) {
        // update status cuti pegawai to REJECTED
        cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
        cutiPegawai.setApprovalLevel(cutiApproval.getApprovalLevel());
        cutiPegawai.setPicSaatIni(new Jabatan(cutiApproval.getJabatan().getId()));

        // save the changes to the database
        repository.save(cutiApproval);
        cutiPegawaiRepository.save(cutiPegawai);
    }

    /**
     * Saves the cuti approval and updates the cuti pegawai status and approval level
     * based on the approval chain.
     * *
     * If the approval chain is null, then it sets the cuti pegawai status to the
     * same as the cuti approval status and sets the approval level to 0 and sets
     * the pic saat ini to the jabatan of the pegawai.
     * *
     * If the approval chain is not null, then it sets the cuti pegawai approval
     * level to the approval level of the approval chain and sets the pic saat ini
     * to the jabatan of the approval chain.
     * *
     *
     * @param cutiApproval  the cuti approval to be saved
     * @param cutiPegawai   the cuti pegawai to be updated
     * @param approvalChain the approval chain
     */
    private void saveReturnReject(CutiApproval cutiApproval, CutiPegawai cutiPegawai, CutiApprovalChain approvalChain) {
        // fetch approval chain
        if (approvalChain == null) {
            // if approval chain is null, then set the cuti pegawai status to the same as the cuti approval status
            // and set the approval level to 0 and set the pic saat ini to the jabatan of the pegawai
            cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
            if (cutiApproval.getApprovalStatus().equals(EApprovalCutiStatus.REJECTED)) {
                cutiPegawai.setApprovalLevel(0);
                cutiPegawai.setPicSaatIni(cutiPegawai.getPegawai().getJabatan());
            }

            if (cutiPegawai.getJenisCuti().getId().equals(jenisCutiTahunan) || cutiPegawai.getJenisCuti().getId().equals(jenisCutiIbadah)) {
                cutiKuotaUpdateByCutiService.updateKuota(cutiPegawai);
            }
        } else {
            // if approval chain is not null, then set the cuti pegawai approval level to the approval level of the approval chain
            // and set the pic saat ini to the jabatan of the approval chain
            cutiPegawai.setApprovalLevel(approvalChain.getApprovalLevel());
            cutiPegawai.setPicSaatIni(new Jabatan(approvalChain.getJabatanId()));
        }
        // save the changes to the database
        repository.save(cutiApproval);
        cutiPegawaiRepository.save(cutiPegawai);
    }

}
