package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "nomor_sp"),
        @Index(columnList = "tanggal_sp"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_sp SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class RiwayatSp extends LampiranSp {
    private String nomorSp;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    private String namaOrganisasi;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private String namaJabatan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSp;
    @ManyToOne
    @JoinColumn(name = "jenis_sp_id", referencedColumnName = "id")
    private JenisSp jenisSp;
    @ManyToOne
    @JoinColumn(name = "sanksi_id", referencedColumnName = "id")
    private Sanksi sanksi;
    private String sanksiNotes;
    private LocalDate tanggalEksekusiSanksi;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private String penandaTangan;
    private String jabatanPenandaTangan;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
