package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CutiApprovalServiceImplTest {
    @Mock
    private CutiPegawaiRepository cutiPegawaiRepository;
    @Mock
    private CutiApprovalChainRepository cutiApprovalChainRepository;
    @Mock
    private PegawaiRepository pegawaiRepository;

    private CutiApprovalPostRequest request;
    private CutiPegawai cutiPegawai;
    private List<CutiApprovalChain> cutiApprovalChains;
    private Pegawai approver;
    private Jabatan jabatanApprover;

    private void generateRequest() {
        request = new CutiApprovalPostRequest();
        request.setCutiId(1L);
        request.setApproverId(278L);
        request.setApprovalLevel(1);
        request.setApprovalStatus(EApprovalCutiStatus.REJECTED);
        request.setNotes("Cuti tahunan");
    }

    private void generateCutiPegawai() {
        Biodata biodata = new Biodata();
        biodata.setNik("123456789");
        biodata.setNama("John Doe");

        Organisasi organisasi = new Organisasi();
        organisasi.setId(65L);
        organisasi.setNama("SUB BAG TEKNOLOGI INFORMASI");

        Jabatan jabatan = new Jabatan();
        jabatan.setId(67L);
        jabatan.setNama("Staff Sub. Bag. Teknologi Informasi");
        jabatan.setOrganisasi(organisasi);

        Pegawai pegawai = new Pegawai();
        pegawai.setId(1L);
        pegawai.setBiodata(biodata);
        pegawai.setNipam("900800456");
        pegawai.setOrganisasi(organisasi);
        pegawai.setJabatan(jabatan);

        CutiJenis cutiJenis = new CutiJenis();
        cutiJenis.setId(1L);
        cutiJenis.setNama("Cuti tahunan");

        jabatanApprover = new Jabatan();
        jabatanApprover.setId(66L);
        jabatanApprover.setNama("Supervisor Teknologi Informasi");
        jabatanApprover.setOrganisasi(organisasi);

        cutiPegawai = new CutiPegawai();
        cutiPegawai.setId(1L);
        cutiPegawai.setPegawai(pegawai);
        cutiPegawai.setNipam(pegawai.getNipam());
        cutiPegawai.setNama(pegawai.getBiodata().getNama());
        cutiPegawai.setOrganisasi(organisasi);
        cutiPegawai.setJabatan(jabatan);
        cutiPegawai.setJenisPengajuanCuti(EJenisPengajuanCuti.PENGAJUAN_CUTI);
        cutiPegawai.setJenisCuti(cutiJenis);
        cutiPegawai.setTanggalMulai(LocalDate.of(2026, 1, 1));
        cutiPegawai.setTanggalSelesai(LocalDate.of(2026, 1, 6));
        cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        cutiPegawai.setApprovalLevel(1);
        cutiPegawai.setPicSaatIni(jabatanApprover);
    }

    private void generateApprover() {
        Biodata bioApprover = new Biodata();
        bioApprover.setNik("456789012");
        bioApprover.setNama("Boss");

        Organisasi organisasi = new Organisasi();
        organisasi.setId(65L);
        organisasi.setNama("SUB BAG TEKNOLOGI INFORMASI");

        approver = new Pegawai();
        approver.setId(278L);
        approver.setBiodata(bioApprover);
        approver.setNipam("890300426");
        approver.setOrganisasi(organisasi);
        approver.setJabatan(jabatanApprover);
    }

    private void generateCutiApprovalChains() {
        cutiApprovalChains = new ArrayList<>();
        cutiApprovalChains.add(new CutiApprovalChain(1L, 1L, 66L, "Supervisor Teknologi Informasi", 1));
        cutiApprovalChains.add(new CutiApprovalChain(2L, 1L, 48L, "Manajer Sumber Daya Manusia & TI", 2));
        cutiApprovalChains.add(new CutiApprovalChain(3L, 1L, 49L, "Supervisor Adm. & Pengembangan SDM", 3));
        cutiApprovalChains.add(new CutiApprovalChain(4L, 1L, 48L, "Manajer Sumber Daya Manusia & TI", 4));
        cutiApprovalChains.add(new CutiApprovalChain(5L, 1L, 25L, "Direktur Umum", 5));
    }

    @BeforeEach
    void setup() {
        this.generateRequest();
        this.generateCutiPegawai();
        this.generateApprover();
        this.generateCutiApprovalChains();
    }

    @Test
    void testIsRequestValid() {
        assertNotNull(request);
        log.info("request: {}", request);
    }

    @Test
    void checkCutiPegawaiIsExist() {
        when(cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING))
                .thenReturn(Optional.of(cutiPegawai));
        Optional<CutiPegawai> cutiPegawaiEntity = cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING);
        assertTrue(cutiPegawaiEntity.isPresent());
        log.info("cuti pegawai: {}", cutiPegawaiEntity.get().getId());
    }

    @Test
    void checkApproverIsExist() {
        when(pegawaiRepository.findById(request.getApproverId())).thenReturn(Optional.of(approver));
        Optional<Pegawai> pegawaiEntity = pegawaiRepository.findById(request.getApproverId());
        assertTrue(pegawaiEntity.isPresent());
        log.info("pegawai: {}", pegawaiEntity.get().getId());
    }

    @Test
    void checkJabatanApproverIsEqualToRequestJabatan() {
        when(pegawaiRepository.findById(request.getApproverId())).thenReturn(Optional.of(approver));
        Optional<Pegawai> pegawaiEntity = pegawaiRepository.findById(request.getApproverId());
        assertTrue(pegawaiEntity.isPresent());
    }

    @Test
    void checkApprovalChainIsExist() {
        when(cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel()))
                .thenReturn(cutiApprovalChains);

        List<CutiApprovalChain> approvalChains = cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel());
        assertEquals(5, approvalChains.size());
        log.info("approval chains: {}", approvalChains);
    }

    @Test
    void validationCheck() {
        when(cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING))
                .thenReturn(Optional.of(cutiPegawai));
        when(pegawaiRepository.findById(request.getApproverId())).thenReturn(Optional.of(approver));
        when(cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel()))
                .thenReturn(cutiApprovalChains);

        Optional<CutiPegawai> cutiPegawaiEntity = cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING);
        Optional<Pegawai> approverEntity = pegawaiRepository.findById(request.getApproverId());

        assertTrue(cutiPegawaiEntity.isPresent());
        assertTrue(approverEntity.isPresent());

        List<CutiApprovalChain> cutiApprovalChainList = cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel());
        boolean approverIsInChains = cutiApprovalChainList.stream().map(CutiApprovalChain::getJabatanId).toList().contains(approverEntity.get().getJabatan().getId());
        assertTrue(approverIsInChains);

        CutiApprovalChain nextApproval = cutiApprovalChainList.get(1);
        assertNotNull(nextApproval);

        CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawaiEntity.get(), approverEntity.get());

        log.info("cuti pegawai: {}", entity);
    }

    @Test
    void testSaveReject() {
        request.setApprovalStatus(EApprovalCutiStatus.REJECTED);
        when(cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING))
                .thenReturn(Optional.of(cutiPegawai));
        when(pegawaiRepository.findById(request.getApproverId())).thenReturn(Optional.of(approver));
        when(cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel()))
                .thenReturn(cutiApprovalChains);

        CutiPegawai cutiPegawaiEntity = cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Cuti Pegawai not found"));
        Pegawai approverEntity = pegawaiRepository.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver Pegawai not found"));

        CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawaiEntity, approverEntity);

        List<CutiApprovalChain> cutiApprovalChainList = cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel());
        boolean approverIsInChains = cutiApprovalChainList.stream().map(CutiApprovalChain::getJabatanId).toList().contains(approverEntity.getJabatan().getId());
        assertTrue(approverIsInChains);

        log.info("cuti approval: {}", entity);
        log.info("cuti pegawai before: {}", cutiPegawaiEntity);
        rejectPengajuan(cutiPegawaiEntity, entity);
        log.info("cuti pegawai after: {}", cutiPegawaiEntity);
    }

    @Test
    void testSaveAccept() {
        request.setApprovalStatus(EApprovalCutiStatus.APPROVED);
        when(cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING))
                .thenReturn(Optional.of(cutiPegawai));
        when(pegawaiRepository.findById(request.getApproverId())).thenReturn(Optional.of(approver));
        when(cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel()))
                .thenReturn(cutiApprovalChains);

        CutiPegawai cutiPegawaiEntity = cutiPegawaiRepository.findByIdAndApprovalCutiStatus(request.getCutiId(), EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Cuti Pegawai not found"));
        Pegawai approverEntity = pegawaiRepository.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver Pegawai not found"));

        CutiApproval entity = CutiApprovalPostRequest.toEntity(request, cutiPegawaiEntity, approverEntity);
        List<CutiApprovalChain> cutiApprovalChainList = cutiApprovalChainRepository.findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(request.getCutiId(), request.getApprovalLevel());
        boolean approverIsInChains = cutiApprovalChainList.stream().map(CutiApprovalChain::getJabatanId).toList().contains(approverEntity.getJabatan().getId());
        assertTrue(approverIsInChains);
        CutiApprovalChain nextApproval = cutiApprovalChainList.get(1);
        assertNotNull(nextApproval);

        acceptPengajuan(cutiPegawaiEntity, entity, nextApproval);
        log.info("cuti pegawai after: {}", cutiPegawaiEntity);
    }

    private void rejectPengajuan(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        cutiPegawai.setApprovalCutiStatus(cutiApproval.getApprovalStatus());
        cutiPegawai.setApprovalLevel(cutiApproval.getApprovalLevel());
        cutiPegawai.setPicSaatIni(new Jabatan(cutiApproval.getJabatan().getId()));
    }

    private void acceptPengajuan(CutiPegawai cutiPegawai, CutiApproval cutiApproval, CutiApprovalChain nextApproval) {
        if (nextApproval == null) {
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.APPROVED);
            cutiPegawai.setApprovalLevel(cutiApproval.getApprovalLevel());
            cutiPegawai.setPicSaatIni(cutiApproval.getJabatan());
        } else {
            cutiPegawai.setApprovalLevel(nextApproval.getApprovalLevel());
            cutiPegawai.setPicSaatIni(new Jabatan(nextApproval.getJabatanId()));
        }
    }
}