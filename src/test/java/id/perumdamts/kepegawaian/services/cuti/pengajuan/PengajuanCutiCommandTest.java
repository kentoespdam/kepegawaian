package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.exceptions.ForbiddenException;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * kepegawaian-p6np: ownership (resolvePemohon) WAJIB dijalankan sebelum validator.
 * Kalau terbalik, error validator (kuota tidak tersedia, sudah ada pending, pernah
 * cuti besar, dst.) membocorkan status data cuti pegawai lain sebelum 403 ownership.
 */
@ExtendWith(MockitoExtension.class)
class PengajuanCutiCommandTest {

    @Mock private RedisHelper redisHelper;
    @Mock private CutiPegawaiRepository repository;
    @Mock private CutiProperties cutiProperties;
    @Mock private SaveCutiService saveCutiService;
    @Mock private HariLiburRepository hariLiburRepository;
    @Mock private PegawaiRepository pegawaiRepository;
    @Mock private CutiJenisRepository cutiJenisRepository;
    @Mock private CutiApprovalChainGenerator cutiApprovalChainGenerator;
    @Mock private CutiPengajuanValidator cutiPengajuanValidator;
    @Mock private CutiOwnershipService ownershipService;

    @InjectMocks private PengajuanCutiCommand command;

    private CutiPengajuanPostRequest request(Long pegawaiId) {
        CutiPengajuanPostRequest request = new CutiPengajuanPostRequest();
        request.setCsrfToken("csrf-test");
        request.setPegawaiId(pegawaiId);
        request.setJenisCutiId(3L); // cuti sakit — non-tahunan, tidak potong kuota
        request.setTanggalMulai(LocalDate.of(2026, 8, 17));
        request.setTanggalSelesai(LocalDate.of(2026, 8, 21));
        request.setJumlahHariKerja(5);
        request.setAlasan("test ownership order");
        return request;
    }

    @Test
    void ownershipResolvedBeforeValidatorOnSave() {
        CutiPengajuanPostRequest request = request(394L);
        Pegawai pegawai = new Pegawai();
        pegawai.setId(394L);
        Biodata biodata = new Biodata("3273012345678901");
        biodata.setNama("Pegawai Test");
        pegawai.setBiodata(biodata);
        pegawai.setNipam("830100446");
        Golongan golongan = new Golongan();
        golongan.setGolongan("III/a");
        golongan.setPangkat("Penata Muda");
        pegawai.setGolongan(golongan);
        pegawai.setOrganisasi(new Organisasi());
        pegawai.setJabatan(new Jabatan());

        when(redisHelper.isTokenAlreadyUsed(any())).thenReturn(false);
        when(ownershipService.resolvePemohon(394L)).thenReturn(pegawai);
        when(cutiJenisRepository.getReferenceById(3L)).thenReturn(new CutiJenis(3L));
        when(hariLiburRepository.findByTanggalBetween(any(), any())).thenReturn(List.of());

        command.save(request);

        InOrder inOrder = inOrder(ownershipService, cutiPengajuanValidator);
        inOrder.verify(ownershipService).resolvePemohon(394L);
        inOrder.verify(cutiPengajuanValidator).validate(request, 394L);
    }

    @Test
    void validatorNotCalledWhenOwnershipRejected() {
        // USER mencoba atas nama pegawai lain → resolvePemohon lempar 403.
        // Validator TIDAK boleh dipanggil sama sekali (tidak boleh bocor error validator).
        CutiPengajuanPostRequest request = request(46L);

        when(redisHelper.isTokenAlreadyUsed(any())).thenReturn(false);
        when(ownershipService.resolvePemohon(46L))
                .thenThrow(new ForbiddenException("Tidak boleh bertindak atas nama pegawai lain"));

        assertThrows(ForbiddenException.class, () -> command.save(request));
        verify(cutiPengajuanValidator, never()).validate(any(), anyLong());
    }
}
