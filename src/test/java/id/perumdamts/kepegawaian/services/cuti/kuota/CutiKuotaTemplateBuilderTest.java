package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Regression: GET /cuti/kuota/template 500
 * "Cannot invoke Biodata.getNama() because Pegawai.getBiodata() is null" —
 * data lama boleh saja punya Pegawai tanpa Biodata, export harus tetap 200.
 */
@ExtendWith(MockitoExtension.class)
class CutiKuotaTemplateBuilderTest {

    @Mock private PegawaiRepository pegawaiRepository;
    @InjectMocks private CutiKuotaTemplateBuilder builder;

    @Test
    void build_doesNotNpe_whenPegawaiHasNoBiodata() {
        Pegawai pegawai = new Pegawai();
        pegawai.setNipam("123456");
        // biodata sengaja null — kondisi data lama yang memicu NPE
        when(pegawaiRepository.findAll(any(Specification.class))).thenReturn(List.of(pegawai));

        ResponseEntity<?> response = builder.build();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ByteArrayResource body = (ByteArrayResource) response.getBody();
        assertNotNull(body, "workbook harus tetap dihasilkan walau ada pegawai tanpa biodata");
        assertTrue(body.getByteArray().length > 0);
    }
}
