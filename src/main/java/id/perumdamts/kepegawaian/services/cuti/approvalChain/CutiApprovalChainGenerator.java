package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CutiApprovalChainGenerator {
    private final CutiApprovalChainRepository repository;
    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiProperties cutiProperties;

    public void forPengajuan(CutiPegawai cutiPegawai) {
        Long jabatanLevelId = cutiPegawai.getJabatan().getLevel().getId();
        List<Long> steps = new ArrayList<>();

        if (jabatanLevelId.equals(cutiProperties.levelManager())) {
            steps.add(cutiProperties.supervisorSdm());
            steps.add(cutiProperties.managerSdm());
            steps.add(cutiProperties.direkturUmum());
            steps.add(cutiProperties.direkturUtama());
        } else if (jabatanLevelId.equals(cutiProperties.levelSupervisor())) {
            Jabatan manager = cutiPegawai.getJabatan().getParent();
            if (manager != null) {
                steps.add(manager.getId());
            }
            steps.add(cutiProperties.supervisorSdm());
            steps.add(cutiProperties.managerSdm());
            steps.add(cutiProperties.direkturUmum());
            steps.add(cutiProperties.direkturUtama());
        } else {
            Jabatan supervisor = cutiPegawai.getJabatan().getParent();
            if (supervisor != null) {
                steps.add(supervisor.getId());
                Jabatan manager = supervisor.getParent();
                if (manager != null) {
                    steps.add(manager.getId());
                }
            }
            steps.add(cutiProperties.supervisorSdm());
            steps.add(cutiProperties.managerSdm());
            steps.add(cutiProperties.direkturUmum());
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
                cutiProperties.supervisorSdm(),
                cutiProperties.managerSdm(),
                cutiProperties.direkturUmum(),
                cutiProperties.direkturUtama()
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

        repository.saveAll(approvalChain);

        Optional<CutiApprovalChain> writeChain = approvalChain.stream()
                .filter(chain -> chain.getReadWriteStatus().equals(EReadWriteStatus.WRITE))
                .findFirst();
        if (writeChain.isPresent()) {
            CutiApprovalChain writeChainEntity = writeChain.get();
            cutiPegawai.setApprovalLevel(writeChainEntity.getApprovalLevel());
            cutiPegawai.setPicSaatIni(new Jabatan(writeChainEntity.getJabatanId()));
            cutiPegawaiRepository.save(cutiPegawai);
        }
    }

    public void forKlaim(CutiPegawai cutiPegawai) {
        List<CutiApprovalChain> approvalChain = new ArrayList<>();
        addApprovalChainIfJabatanExists(approvalChain, cutiPegawai, cutiProperties.supervisorSdm());
        if (!approvalChain.isEmpty()) {
            approvalChain.getFirst().setApprovalStatus(EApprovalCutiStatus.PENDING);
            approvalChain.getFirst().setReadWriteStatus(EReadWriteStatus.WRITE);
            repository.saveAll(approvalChain);
        }
    }

    private void addApprovalChainIfJabatanExists(
            List<CutiApprovalChain> approvalChain,
            CutiPegawai cutiPegawai,
            Long jabatanId
    ) {
        jabatanRepository.findById(jabatanId)
                .map(jabatan -> new CutiApprovalChain(
                        cutiPegawai,
                        jabatan.getId(),
                        jabatan.getNama(),
                        1
                ))
                .ifPresent(approvalChain::add);
    }
}
