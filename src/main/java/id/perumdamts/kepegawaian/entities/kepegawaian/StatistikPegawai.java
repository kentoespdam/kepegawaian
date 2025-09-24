package id.perumdamts.kepegawaian.entities.kepegawaian;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "bulan_idx", columnList = "bulan"),
        @Index(name = "tahun_idx", columnList = "tahun"),
        @Index(name = "pendidikan_idx", columnList = "pendidikan"),
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bulan", "tahun", "pendidikan"})
})
@Getter
@Setter
@ToString
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
    @Column(name = "golongan_a")
    private Integer golonganA;
    @Column(name = "golongan_b")
    private Integer golonganB;
    @Column(name = "golongan_c")
    private Integer golonganC;
    @Column(name = "golongan_d")
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
