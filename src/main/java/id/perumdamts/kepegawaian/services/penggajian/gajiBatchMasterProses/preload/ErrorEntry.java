package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;

import java.io.Serializable;

public record ErrorEntry(
        String nipam,
        String nama,
        EJenisErrorGaji jenis,
        String notes
) implements Serializable {
}
