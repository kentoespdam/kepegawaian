package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "nomor_kartu"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE kartu_identitas SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
public class KartuIdentitas extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "nik", referencedColumnName = "nik")
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "jenis_kitas_id", referencedColumnName = "id")
    private JenisKitas jenisKartu;
    private String nomorKartu;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalExpired;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerima;
    private String notes;

    public KartuIdentitas(Biodata biodata) {
        JenisKitas jenisKitas = new JenisKitas(1L);
        this.biodata = biodata;
        this.jenisKartu = jenisKitas;
        this.nomorKartu = biodata.getNik();
    }
}
