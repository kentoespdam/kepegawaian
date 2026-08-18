package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKlaimDetailRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-ciw: settlement klaim harus didispatch berdasarkan PERIODE refCuti
 * (keputusan dibuat saat pengajuan), bukan re-klasifikasi tanggal klaim dengan now()
 * — yang dulu bisa crash (IllegalArgumentException) atau salah bucket saat approval
 * lintas tahun.
 */
@ExtendWith(MockitoExtension.class)
class KlaimCutiCommandTest {

    @Mock private RedisHelper redisHelper;
    @Mock private CutiPegawaiRepository cutiPegawaiRepository;
    @Mock private CutiKlaimValidator cutiKlaimValidator;
    @Mock private HariLiburRepository hariLiburRepository;
    @Mock private CutiKlaimDetailRepository cutiKlaimDetailRepository;
    @Mock private CutiApprovalChainRepository cutiApprovalChainRepository;
    @Mock private PegawaiRepository pegawaiRepository;
    @Mock private CutiApprovalChainGenerator cutiApprovalChainGenerator;
    @Mock private CutiApproveKlaimCutiService cutiApproveKlaimCutiService;
    @Mock private CutiKlaimCrossYearSettlement cutiKlaimCrossYearSettlement;
    @Mock private CutiOwnershipService ownershipService;

    @InjectMocks private KlaimCutiCommand command;

    private CutiApprovalPostRequest request(Long cutiId, Long approverId) {
        CutiApprovalPostRequest req = new CutiApprovalPostRequest();
        req.setCsrfToken("csrf-test");
        req.setCutiId(cutiId);
        req.setApproverId(approverId);
        req.setApprovalLevel(1);
        req.setApprovalStatus(EApprovalCutiStatus.APPROVED);
        req.setNotes("setuju");
        return req;
    }

    private Pegawai approver(Long jabatanId) {
        Pegawai p = new Pegawai();
        p.setId(900L);
        Jabatan jabatan = new Jabatan(jabatanId);
        jabatan.setNama("Supervisor SDM");
        p.setJabatan(jabatan);
        return p;
    }

    private CutiPegawai refCuti(LocalDate mulai, LocalDate selesai, LocalDateTime createdAt) {
        CutiPegawai r = new CutiPegawai();
        r.setId(100L);
        r.setTanggalMulai(mulai);
        r.setTanggalSelesai(selesai);
        r.setCreatedAt(createdAt);
        r.setRiwayatKuota0(20);
        r.setRiwayatPakai0(4);
        r.setRiwayatSisa0(16);
        r.setRiwayatKuota1(10);
        r.setRiwayatPakai1(2);
        r.setRiwayatSisa1(8);
        return r;
    }

    private CutiPegawai claim(CutiPegawai ref, LocalDate mulai, LocalDate selesai) {
        CutiPegawai c = new CutiPegawai();
        c.setId(200L);
        c.setRefCuti(ref);
        c.setPicSaatIni(new Jabatan(99L));
        c.setTanggalMulai(mulai);
        c.setTanggalSelesai(selesai);
        c.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);
        return c;
    }

    @Test
    void crossYearClaimDispatchesViaRefCutiPeriodInsteadOfNow() {
        // refCuti OVERLAPPING (Des 2025–Jan 2026). Klaim lintas tahun yang disetujui
        // di tahun mana pun TIDAK boleh crash / salah bucket.
        CutiPegawai ref = refCuti(LocalDate.of(2025, 12, 20), LocalDate.of(2026, 1, 10),
                LocalDateTime.of(2025, 12, 10, 9, 0));
        CutiPegawai claim = claim(ref, LocalDate.of(2025, 12, 29), LocalDate.of(2026, 1, 4));
        Pegawai approver = approver(99L);

        when(redisHelper.isTokenAlreadyUsed("csrf-test")).thenReturn(false);
        when(cutiPegawaiRepository.findById(200L)).thenReturn(Optional.of(claim));
        when(pegawaiRepository.findById(900L)).thenReturn(Optional.of(approver));

        command.saveKlaim(request(200L, 900L));

        verify(cutiKlaimCrossYearSettlement).overlappingYear(same(claim), any(CutiApproval.class));
        verify(cutiApproveKlaimCutiService, never()).between1JanAnd30Jun(any(), any());
        verify(cutiApproveKlaimCutiService, never()).between1JulAnd31Dec(any(), any());
    }

    @Test
    void sameYearClaimFollowsRefCutiPeriodNotClaimDates() {
        // Klaim parsial: tanggal klaim seluruhnya di 2025, tapi refCuti menyeberang ke 2026.
        // Settlement harus tetap mengikuti PERIODE refCuti (OVERLAPPING), bukan tanggal klaim.
        CutiPegawai ref = refCuti(LocalDate.of(2025, 12, 20), LocalDate.of(2026, 1, 10),
                LocalDateTime.of(2025, 12, 10, 9, 0));
        CutiPegawai claim = claim(ref, LocalDate.of(2025, 12, 29), LocalDate.of(2025, 12, 31));
        Pegawai approver = approver(99L);

        when(redisHelper.isTokenAlreadyUsed("csrf-test")).thenReturn(false);
        when(cutiPegawaiRepository.findById(200L)).thenReturn(Optional.of(claim));
        when(pegawaiRepository.findById(900L)).thenReturn(Optional.of(approver));

        command.saveKlaim(request(200L, 900L));

        verify(cutiKlaimCrossYearSettlement).overlappingYear(same(claim), any(CutiApproval.class));
        verify(cutiApproveKlaimCutiService, never()).between1JanAnd30Jun(any(), any());
        verify(cutiApproveKlaimCutiService, never()).between1JulAnd31Dec(any(), any());
    }
}
