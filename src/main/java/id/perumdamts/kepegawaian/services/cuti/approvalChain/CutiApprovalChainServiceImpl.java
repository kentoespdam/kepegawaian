package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
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

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;
    @Value("${custom.jabatan.managerSdm}")
    private Long managerSdm;
    @Value("${custom.jabatan.direkturUtama}")
    private Long direkturUtama;
    @Value("${custom.jabatan.direkturTeknik}")
    private Long direkturTeknik;
    @Value("${custom.jabatan.direkturUmum}")
    private Long direkturUmum;

    @Value("${custom.levelJabatan.staff}")
    private Long levelStaf;
    @Value("${custom.levelJabatan.supervisor}")
    private Long levelSupervisor;
    @Value("${custom.levelJabatan.manager}")
    private Long levelManager;

    @Override
    public Page<CutiApprovalChainResponse> findCutiPegawai(CutiApprovalChainRequest request) {
        return repository.findAll(request.getApprovalChainSpecification(), request.getPageable())
                .map(CutiApprovalChainResponse::from);
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
    public void generateApprovalChain(CutiPegawai cutiPegawai) {
        Long jabatanLevelId = cutiPegawai.getJabatan().getLevel().getId();
        if (jabatanLevelId.equals(levelManager))
            this.levelManager(cutiPegawai);
        else if (jabatanLevelId.equals(levelSupervisor))
            this.levelSupervisor(cutiPegawai);
        else
            this.levelStaf(cutiPegawai);
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
     * Generates the approval chain for a given {@link CutiPegawai} who is a staf.
     * The approval chain consists of the following:
     * <ol>
     *     <li>The supervisor of the given {@link CutiPegawai}</li>
     *     <li>The manager of the given {@link CutiPegawai}</li>
     *     <li>The supervisor SDM if it exists</li>
     *     <li>The manager SDM if it exists</li>
     *     <li>The direktur umum if it exists</li>
     * </ol>
     *
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for
     */
    private void levelStaf(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();

        Jabatan supervisor = cutiPegawai.getJabatan().getParent();
        Jabatan manager = supervisor.getParent();

        approvalChain.add(new CutiApprovalChain(cutiPegawai, supervisor.getId(), supervisor.getNama(), 1));
        approvalChain.add(new CutiApprovalChain(cutiPegawai, manager.getId(), manager.getNama(), 2));

        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 3);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 4);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 5);

        repository.saveAll(approvalChain);
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
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for
     */
    private void levelSupervisor(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();

        Jabatan manager = cutiPegawai.getJabatan().getParent();
        approvalChain.add(new CutiApprovalChain(cutiPegawai, manager.getId(), manager.getNama(), 1));

        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 2);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 3);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 4);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUtama, 5);

        repository.saveAll(approvalChain);
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
     * @param cutiPegawai the {@link CutiPegawai} to generate the approval chain for
     */
    private void levelManager(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();

        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, supervisorSdm, 1);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, managerSdm, 2);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUmum, 3);
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, direkturUtama, 4);

        repository.saveAll(approvalChain);
    }
}
