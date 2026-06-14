package id.perumdamts.kepegawaian.services.master.jenisMutasi;

import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiResponse;

import java.util.List;

public interface JenisMutasiService {
    List<JenisMutasiResponse> findAll();
}
