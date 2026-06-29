package id.perumdamts.kepegawaian.services.pegawai.port;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

/**
 * Port interface for bootstrapping Kontrak actions from Pegawai Command Service.
 * Implemented by RiwayatKontrakCommandService in Kepegawaian module.
 * Refer to ADR-0023.
 */
public interface KontrakBootstrapPort {
    RiwayatKontrak createKontrakFromPegawai(PegawaiPostRequest request, Pegawai pegawai);
}
