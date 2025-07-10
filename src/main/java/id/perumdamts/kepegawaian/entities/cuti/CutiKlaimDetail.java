package id.perumdamts.kepegawaian.entities.cuti;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table
@Data
@NoArgsConstructor
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
