package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PegawaiServiceTest {
    @Mock
    private PegawaiRepository repository;
    @Mock
    private GolonganRepository golonganRepository;

    private Pegawai pegawai;
    private Golongan golongan;

    @BeforeEach
    void setup() {
        Biodata biodata = new Biodata();
        biodata.setNik("123456789");
        biodata.setNama("John Doe");

        pegawai = new Pegawai();
        pegawai.setId(1L);
        pegawai.setBiodata(biodata);
        pegawai.setNipam("123456789");
        pegawai.setStatusPegawai(EStatusPegawai.PEGAWAI);
        pegawai.setGajiPokok(1_000_000D);

        golongan = new Golongan();
        golongan.setId(1L);
        golongan.setGolongan("A");
        golongan.setPangkat("Pangkat A");

        RiwayatSk riwayatSk = new RiwayatSk();
        riwayatSk.setPegawai(pegawai);
        riwayatSk.setNomorSk("123456789");
        riwayatSk.setJenisSk(EJenisSk.SK_KENAIKAN_GAJI_BERKALA);
        riwayatSk.setTanggalSk(LocalDate.now());
        riwayatSk.setTmtBerlaku(LocalDate.now());
        riwayatSk.setGolongan(golongan);
        riwayatSk.setGajiPokok(1_000_000D);
        riwayatSk.setNotes("initial notes");
    }

    @Test
    void findById_WhenFound_ShouldReturnResponse() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(pegawai));

        Pegawai result = repository.findById(id).orElse(null);

        assertNotNull(result);
        assertEquals(pegawai.getId(), result.getId());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void update_sk_list() {
        Long id = 1L;
        RiwayatSkPostRequest request = new RiwayatSkPostRequest();
        request.setPegawaiId(pegawai.getId());
        request.setNomorSk("123456789");
        request.setNotes("initial notes");

        when(repository.findById(id)).thenReturn(Optional.of(pegawai));
        when(golonganRepository.findById(request.getGolonganId())).thenReturn(Optional.of(golongan));

        Golongan golonganById = golonganRepository.findById(request.getGolonganId()).orElse(null);
        assertNotNull(golonganById);

        Pegawai pegawaiById = repository.findById(id).orElse(null);
        assertNotNull(pegawaiById);

        pegawai.setGolongan(golonganById);
        RiwayatSk entity = RiwayatSkPostRequest.toEntity(request, pegawai);
        entity.setId(1L);

        assertNotNull(entity);
        assertEquals(request.getPegawaiId(), entity.getPegawai().getId());
        assertEquals(request.getNomorSk(), entity.getNomorSk());
        assertEquals(request.getNotes(), entity.getNotes());

        log.info("riwayatSk: {}", entity);

        pegawai.getRiwayatSkList().add(entity);
        assertEquals(1, pegawai.getRiwayatSkList().size());


//        when(riwayatSkRepository.save(any(RiwayatSk.class))).thenReturn(entity);



    }
}