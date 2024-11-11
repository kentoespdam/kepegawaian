package id.perumdamts.kepegawaian.services.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import org.springframework.data.domain.Page;

public interface GajiTunjanganService {
    Page<GajiTunjanganResponse> findPage(GajiTunjanganRequest request);

    GajiTunjanganResponse findById(EJenisTunjangan jenis, Long id);

    SavedStatus<?> save(EJenisTunjangan jenis, GajiTunjanganPostRequest request);

    SavedStatus<?> update(EJenisTunjangan jenis, Long id, GajiTunjanganPutRequest request);

    boolean deleteById(EJenisTunjangan jenis, Long id);
}
