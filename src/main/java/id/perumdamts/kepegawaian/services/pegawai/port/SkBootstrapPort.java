package id.perumdamts.kepegawaian.services.pegawai.port;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

/**
 * Port interface for bootstrapping SK actions from Pegawai Command Service.
 * Implemented by RiwayatSkCommandService in Kepegawaian module.
 * Refer to ADR-0023.
 */
public interface SkBootstrapPort {
    RiwayatSk createSkCapeg(PegawaiPostRequest request, Pegawai pegawai);
    RiwayatSk createSkPegawaiTetap(PegawaiPostRequest request, Pegawai pegawai);
}
