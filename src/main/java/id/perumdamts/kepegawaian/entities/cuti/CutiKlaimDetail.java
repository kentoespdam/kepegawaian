package id.perumdamts.kepegawaian.entities.cuti;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CutiKlaimDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ref_cuti_id", referencedColumnName = "id")
    private CutiPegawai refCuti;
    private LocalDate tanggal;

    public CutiKlaimDetail(CutiPegawai ref, LocalDate tanggal) {
        this.refCuti = ref;
        this.tanggal = tanggal;
    }
}
