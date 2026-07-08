package id.perumdamts.kepegawaian.dto.master.hariLibur;

import java.time.LocalDate;

public record HariLiburListResponse(Long id, LocalDate tanggal, String jenisLibur) {}
