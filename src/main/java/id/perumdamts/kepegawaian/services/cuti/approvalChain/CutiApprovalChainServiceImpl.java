package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import lombok.RequiredArgsConstructor;
import id.perumdamts.kepegawaian.config.CutiProperties;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CutiApprovalChainServiceImpl implements CutiApprovalChainService {
    private final CutiApprovalChainRepository repository;
    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiProperties cutiProperties;

    @Override
    public Page<CutiApprovalChainResponse> findCutiPegawai(CutiApprovalChainRequest request) {
        return repository.findPageApproval(request);
    }

    /**
     * Generates the approval chain for a given {@link CutiPegawai}
     * <p>
     * urutan approval (staff)
     * 1. SUPERVISOR (atasan langsung) (jika tidak ada skip ke step selanjutnya)
     * 2. MANAGER (atasan langsung) (jika tidak ada skip ke step selanjutnya)
     * 3. SUPERVISOR HRD
     * 4. MANAGER HRD
     * 5. Direksi (DIREKTUR UMUM) (jika dibawah Direktur Teknik maka berikan notifikasi kepada Direktur Teknik)
     * *
     * urutan approval (supervisor)
     * 1. MANAGER (atasan langsung) (jika tidak ada skip ke step selanjutnya)
     * 2. SUPERVISOR HRD
     * 3. MANAGER HRD
     * 4. Direksi (DIREKTUR UMUM) (jika dibawah Direktur Teknik maka berikan notifikasi kepada Direktur Teknik)
     * 5. DIREKTUR UTAMA
     * *
     * urutan approval (manager)
     * 1. SUPERVISOR HRD
     * 2. MANAGER HRD (jika Pembuat Pengajuan MANAGER HRD maka skip ke step selanjutnya)
     * 3. Direksi (DIREKTUR UMUM) (jika dibawah Direktur Teknik maka berikan notifikasi kepada Direktur Teknik)
     * 4. DIREKTUR UTAMA
     *
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for
     */
    @Override
    public List<CutiApprovalChain> generateApprovalChain(CutiPegawai cutiPegawai) {
        Long jabatanLevelId = cutiPegawai.getJabatan().getLevel().getId();
        List<Long> steps = new ArrayList<>();

        if (jabatanLevelId.equals(cutiProperties.getLevelManager())) {
            // Manager
            steps.add(cutiProperties.getSupervisorSdm());
            steps.add(cutiProperties.getManagerSdm());
            steps.add(cutiProperties.getDirekturUmum());
            steps.add(cutiProperties.getDirekturUtama());
        } else if (jabatanLevelId.equals(cutiProperties.getLevelSupervisor())) {
            // Supervisor
            Jabatan manager = cutiPegawai.getJabatan().getParent();
            if (manager != null) {
                steps.add(manager.getId());
            }
            steps.add(cutiProperties.getSupervisorSdm());
            steps.add(cutiProperties.getManagerSdm());
            steps.add(cutiProperties.getDirekturUmum());
            steps.add(cutiProperties.getDirekturUtama());
        } else {
            // Staff/Other
            Jabatan supervisor = cutiPegawai.getJabatan().getParent();
            if (supervisor != null) {
                steps.add(supervisor.getId());
                Jabatan manager = supervisor.getParent();
                if (manager != null) {
                    steps.add(manager.getId());
                }
            }
            steps.add(cutiProperties.getSupervisorSdm());
            steps.add(cutiProperties.getManagerSdm());
            steps.add(cutiProperties.getDirekturUmum());
        }

        List<Long> cleanSteps = steps.stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        List<CutiApprovalChain> approvalChain = new ArrayList<>();
        int sequence = 1;
        for (Long jabatanId : cleanSteps) {
            Jabatan jab = jabatanRepository.findById(jabatanId).orElse(null);
            if (jab != null) {
                CutiApprovalChain step = new CutiApprovalChain(cutiPegawai, jab.getId(), jab.getNama(), sequence++);
                step.setApprovalStatus(EApprovalCutiStatus.PENDING);
                step.setReadWriteStatus(EReadWriteStatus.READ);
                approvalChain.add(step);
            }
        }

        java.util.Set<Long> hrAndDireksiIds = java.util.Set.of(
                cutiProperties.getSupervisorSdm(),
                cutiProperties.getManagerSdm(),
                cutiProperties.getDirekturUmum(),
                cutiProperties.getDirekturUtama()
        );

        boolean foundWrite = false;
        for (CutiApprovalChain step : approvalChain) {
            boolean active = pegawaiRepository.existsByJabatanId(step.getJabatanId());
            if (!active && !foundWrite && !hrAndDireksiIds.contains(step.getJabatanId())) {
                step.setReadWriteStatus(EReadWriteStatus.READ);
                step.setApprovalStatus(EApprovalCutiStatus.APPROVED);
            } else if (!foundWrite) {
                step.setReadWriteStatus(EReadWriteStatus.WRITE);
                step.setApprovalStatus(EApprovalCutiStatus.PENDING);
                foundWrite = true;
            } else {
                step.setReadWriteStatus(EReadWriteStatus.READ);
                step.setApprovalStatus(EApprovalCutiStatus.PENDING);
            }
        }

        return repository.saveAll(approvalChain);
    }

    @Override
    public void generateApprovalKlaimChain(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, cutiProperties.getSupervisorSdm(), 1);
        approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.PENDING);
        approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.WRITE);

        repository.saveAll(approvalChain);
    }

    private void addApprovalChainIfJabatanExists(
            List<CutiApprovalChain> approvalChain,
            CutiPegawai cutiPegawai,
            Long jabatanId,
            int sequence
    ) {
        jabatanRepository.findById(jabatanId)
                .map(jabatan -> new CutiApprovalChain(
                        cutiPegawai,
                        jabatan.getId(),
                        jabatan.getNama(),
                        sequence
                ))
                .ifPresent(approvalChain::add);
    }
}
