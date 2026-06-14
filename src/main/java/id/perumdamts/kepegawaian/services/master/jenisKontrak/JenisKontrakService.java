package id.perumdamts.kepegawaian.services.master.jenisKontrak;

import id.perumdamts.kepegawaian.dto.master.jenisKontrak.JenisKontrakResponse;

import java.util.List;

public interface JenisKontrakService {
    List<JenisKontrakResponse> findAll();
}
