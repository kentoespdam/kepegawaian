package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gaji_batch_root_lampiran")
@Data
public class GajiBatchRootLampiran implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "root_batch_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"errorLogs", "lampiranList"})
    private GajiBatchRoot gajiBatchRoot;
    @Enumerated(EnumType.ORDINAL)
    private EJenisPotonganGaji jenisLampiranGaji;
    private String mimeType;
    private String fileName;
    private String hashedFileName;

    public GajiBatchRootLampiran(GajiBatchRoot gajiBatchRoot, EJenisPotonganGaji eJenisPotonganGaji, String mimeType, String fileName, String hashedFileName) {
        this.gajiBatchRoot = gajiBatchRoot;
        this.jenisLampiranGaji = eJenisPotonganGaji;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.hashedFileName = hashedFileName;
    }

    @Override
    public String toString() {
        return "GajiBatchRootLampiran{" +
                "gajiBatchRootId=" + gajiBatchRoot.getId() +
                ", jenisLampiranGaji=" + jenisLampiranGaji +
                ", mimeType='" + mimeType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", hashedFileName='" + hashedFileName + '\'' +
                '}';
    }
}

