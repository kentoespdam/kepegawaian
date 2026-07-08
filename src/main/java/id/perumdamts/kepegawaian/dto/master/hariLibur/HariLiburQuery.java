package id.perumdamts.kepegawaian.dto.master.hariLibur;

import java.time.LocalDate;

public record HariLiburQuery(Long id, LocalDate tanggal, String jenisLibur, String notes) {}
