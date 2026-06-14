package id.perumdamts.kepegawaian.services.master.jenisSk;

import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkResponse;

import java.util.List;

public interface JenisSkService {
    List<JenisSkResponse> findAll();
}
