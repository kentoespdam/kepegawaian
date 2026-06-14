package id.perumdamts.kepegawaian.services.master.statusPegawai;

import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;

import java.util.List;

public interface StatusPegawaiService {
    List<StatusPegawaiResponse> findAll();
}
