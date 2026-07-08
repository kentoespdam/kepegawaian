package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;

import java.time.LocalDateTime;

public record LampiranProfilQuery(
        Long id,
        EJenisLampiranProfil ref,
        Long refId,
        String fileName,
        String mimeType,
        String notes,
        Boolean disetujui,
        String disetujuiOleh,
        LocalDateTime tanggalDisetujui
) {}
