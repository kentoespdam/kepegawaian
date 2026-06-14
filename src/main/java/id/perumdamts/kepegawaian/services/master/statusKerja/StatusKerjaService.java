package id.perumdamts.kepegawaian.services.master.statusKerja;

import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaResponse;

import java.util.List;

public interface StatusKerjaService {
    List<StatusKerjaResponse> findAll();
}
