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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;
    @Value("${custom.jabatan.managerSdm}")
    private Long managerSdm;
    @Value("${custom.jabatan.direkturUtama}")
    private Long direkturUtama;
    @Value("${custom.jabatan.direkturUmum}")
    private Long direkturUmum;

    @Value("${custom.levelJabatan.supervisor}")
    private Long levelSupervisor;
    @Value("${custom.levelJabatan.manager}")
    private Long levelManager;

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
        if (jabatanLevelId.equals(levelManager))
            return this.levelManagerList(cutiPegawai);
        else if (jabatanLevelId.equals(levelSupervisor))
            return this.levelSupervisorList(cutiPegawai);
        else
            return this.levelStafList(cutiPegawai);
    }

    @Override
    public void generateApprovalKlaimChain(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 1);
        approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.PENDING);
        approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.WRITE);

        repository.saveAll(approvalChain);
    }


    /**
     * Adds a new {@link CutiApprovalChain} to the given list of chains if the given
     * jabatan exists.
     *
     * @param approvalChain the list of approval chains
     * @param cutiPegawai   the {@link CutiPegawai} to add the approval chain to
     * @param jabatanId     the ID of the jabatan to add to the approval chain
     * @param sequence      the sequence number of the approval chain
     */
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

    /**
     * Generates the approval chain for a given {@link CutiPegawai} who is a staff.
     * The approval chain consists of the following:
     * <ol>
     *     <li>The supervisor of the given {@link CutiPegawai}</li>
     *     <li>The manager of the given {@link CutiPegawai}</li>
     *     <li>The supervisor SDM if it exists</li>
     *     <li>The manager SDM if it exists</li>
     *     <li>The direktur umum if it exists</li>
     * </ol>
     *
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for (staff)
     * @return the list of approval chains
     */
    private List<CutiApprovalChain> levelStafList(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();

        // Get the supervisor and manager of the given CutiPegawai
        Jabatan supervisor = cutiPegawai.getJabatan().getParent();
        boolean supervisorExists = pegawaiRepository.existsByJabatanId(supervisor.getId());
        Jabatan manager = supervisor.getParent();
        boolean managerExists = pegawaiRepository.existsByJabatanId(manager.getId());

        // Add the approval chain for the supervisor
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisor.getId(), 1);
        // Add the approval chain for the manager
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, manager.getId(), 2);
        // Add the approval chain for the supervisor SDM
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 3);
        // Add the approval chain for the manager SDM
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 4);
        // Add the approval chain for the direktur umum
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 5);

        // Set the read/write status of the approval chain based on the existence of the supervisor and manager
        if (supervisorExists) {
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.WRITE);
        } else if (managerExists) {
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.READ);
            approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.APPROVED);
            approvalChain.get(1).setReadWriteStatus(EReadWriteStatus.WRITE);
        } else {
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.READ);
            approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.APPROVED);
            approvalChain.get(1).setReadWriteStatus(EReadWriteStatus.READ);
            approvalChain.get(1).setApprovalStatus(EApprovalCutiStatus.APPROVED);
            approvalChain.get(2).setReadWriteStatus(EReadWriteStatus.WRITE);
        }
        return repository.saveAll(approvalChain);
    }


    /**
     * Generates the approval chain for a given {@link CutiPegawai} who is a supervisor.
     * The approval chain consists of the following:
     * <ol>
     *     <li>The manager of the given {@link CutiPegawai}</li>
     *     <li>The supervisor SDM if it exists</li>
     *     <li>The manager SDM if it exists</li>
     *     <li>The direktur umum if it exists</li>
     *     <li>The direktur utama if it exists</li>
     * </ol>
     *
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for (supervisor)
     * @return the list of approval chains
     */
    private List<CutiApprovalChain> levelSupervisorList(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();

        Jabatan manager = cutiPegawai.getJabatan().getParent();
        boolean isMgrExists = pegawaiRepository.existsByJabatanId(manager.getId());
        approvalChain.add(new CutiApprovalChain(cutiPegawai, manager.getId(), manager.getNama(), 1));

        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 2);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 3);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 4);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUtama, 5);

        if (isMgrExists) {
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.WRITE);
        } else {
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.READ);
            approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.APPROVED);
            approvalChain.get(1).setReadWriteStatus(EReadWriteStatus.WRITE);
        }

        return repository.saveAll(approvalChain);
    }

    /**
     * Generates the approval chain for a given {@link CutiPegawai} who is a manager.
     * The approval chain consists of the following:
     * <ol>
     *     <li>The supervisor SDM if it exists</li>
     *     <li>The manager SDM if it exists</li>
     *     <li>The direktur umum if it exists</li>
     *     <li>The direktur utama if it exists</li>
     * </ol>
     *
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for (manager)
     * @return the list of approval chains
     */
    private List<CutiApprovalChain> levelManagerList(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();
        approvalChain.add(new CutiApprovalChain(cutiPegawai, supervisorSdm, "Supervisor Adm. & Pengembangan SDM", 1, EApprovalCutiStatus.PENDING, EReadWriteStatus.WRITE));

        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 2);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 3);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUtama, 4);

        return repository.saveAll(approvalChain);
    }
}
