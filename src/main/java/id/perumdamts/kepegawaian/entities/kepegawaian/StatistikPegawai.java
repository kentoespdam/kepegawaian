package id.perumdamts.kepegawaian.entities.kepegawaian;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(name = "bulan_idx", columnList = "bulan"),
        @Index(name = "tahun_idx", columnList = "tahun"),
        @Index(name = "pendidikan_idx", columnList = "pendidikan"),
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bulan", "tahun", "pendidikan"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistikPegawai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer bulan;
    private Integer tahun;
    private Integer seq;
    private String pendidikan;
    private Integer nonGolongan;
    private Integer golonganA;
    private Integer golonganB;
    private Integer golonganC;
    private Integer golonganD;
    private Integer kontrak;
    private Integer capeg;
    private Integer honorer;
    private Integer tetap;
    private Integer adm;
    private Integer pelayanan;
    private Integer teknik;
    private Integer pria;
    private Integer wanita;
}
