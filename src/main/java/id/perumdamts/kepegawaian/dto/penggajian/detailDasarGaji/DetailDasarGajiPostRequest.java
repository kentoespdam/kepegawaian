package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class DetailDasarGajiPostRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Long golonganId;
    private Double nominal;

    @JsonIgnore
    public Specification<DetailDasarGaji> getSpecification() {
        return SpecificationBuilder.<DetailDasarGaji>of()
                .addEqual(dasarGajiId, "dasarGaji", "id")
                .addEqual(mkg, "mkg")
                .addEqual(golonganId, "golongan", "id")
                .addEqual(nominal, "nominal")
                .build();
    }

    public static DetailDasarGaji toEntity(DetailDasarGajiPostRequest request, DasarGaji dasarGaji, Golongan golongan) {
        Integer golonganKode = Integer.parseInt(golongan.getGolongan().split("\\.")[1]);
        DetailDasarGaji entity = new DetailDasarGaji();
        entity.setDasarGaji(dasarGaji);
        entity.setMkg(request.getMkg());
        entity.setGolonganKode(golonganKode);
        entity.setNominal(request.getNominal());
        return entity;
    }
}
