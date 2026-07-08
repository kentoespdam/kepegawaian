package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiReadMapper;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatTerminasiQueryRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jooq.PegawaiQueryRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RiwayatTerminasiQueryService {
    private final RiwayatTerminasiQueryRepository queryRepository;
    private final PegawaiQueryRepository pegawaiQueryRepository;
    private final PegawaiRepository pegawaiRepository;

    public Page<RiwayatTerminasiQuery> findPage(RiwayatTerminasiRequest request) {
        return queryRepository.pageQuery(request)
                .map(q -> {
                    PegawaiResponse pegawai = pegawaiQueryRepository.findByNipam(q.nipam()).orElse(null);
                    return new RiwayatTerminasiQuery(
                            q.id(), q.alasanTerminasi(), pegawai, q.nipam(), q.nama(), q.nomorSk(),
                            q.skTerminasi(), q.lampiranSkTerminasi(), q.organisasi(), q.namaOrganisasi(),
                            q.jabatan(), q.namaJabatan(), q.golongan(), q.namaGolongan(),
                            q.tanggalTerminasi(), q.tahunTerminasi(), q.masaKerja(), q.notes()
                    );
                });
    }

    public Page<PegawaiResponse> findPageCalonPensiun(RiwayatTerminasiRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusMonths(3);
        request.setTanggalTerminasi(end);
        request.setSortBy("Biodata.nama");
        request.setSortDirection("ASC");

        return pegawaiRepository.findAll(request.getCalonPensiunSpecification(), request.getPageable())
                .map(PegawaiReadMapper::toResponse);
    }

    public RiwayatTerminasiQuery findById(Long id) {
        RiwayatTerminasiQuery q = queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat Terminasi not found"));
        PegawaiResponse pegawai = pegawaiQueryRepository.findByNipam(q.nipam()).orElse(null);
        return new RiwayatTerminasiQuery(
                q.id(), q.alasanTerminasi(), pegawai, q.nipam(), q.nama(), q.nomorSk(),
                q.skTerminasi(), q.lampiranSkTerminasi(), q.organisasi(), q.namaOrganisasi(),
                q.jabatan(), q.namaJabatan(), q.golongan(), q.namaGolongan(),
                q.tanggalTerminasi(), q.tahunTerminasi(), q.masaKerja(), q.notes()
        );
    }
}
