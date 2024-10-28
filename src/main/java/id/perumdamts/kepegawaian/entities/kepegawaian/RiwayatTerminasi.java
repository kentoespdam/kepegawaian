package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {
        @Index(columnList = "nipam"),
        @Index(columnList = "nama"),
        @Index(columnList = "nomor_sk"),
        @Index(columnList = "tanggal_terminasi")
})
@SQLDelete(sql = "UPDATE riwayat_terminasi SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class RiwayatTerminasi extends IdsAbstract {
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;
    private String nomorSk;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "riwayat_sk_id", referencedColumnName = "id")
    private RiwayatSk skTerminasi;
    @OneToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    private String namaOrganisasi;
    @OneToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private String namaJabatan;
    @OneToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    private String namaGolongan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalTerminasi;
    private Integer tahunTerminasi;
    private Integer masaKerja;
    private String notes;
}

