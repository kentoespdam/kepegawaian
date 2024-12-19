package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted"),
        @Index(columnList = "tgl_proses"),
        @Index(columnList = "tglVerifikasiTahap1"),
        @Index(columnList = "tglVerifikasiTahap2"),
        @Index(columnList = "tglPersetujuan"),
        @Index(columnList = "status"),
        @Index(columnList = "periode")
})
@Data
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE gaji_root_batch SET is_deleted=true WHERE batchId=?")
@SQLRestriction("is_deleted = false")
public class GajiBatchRoot implements Serializable {
    @Id
    private String batchId;
    private String periode;
    @Enumerated(EnumType.ORDINAL)
    private EProsesGaji status = EProsesGaji.PENDING;
    private Integer totalPegawai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglProses;
    private String diProsesOleh;
    private String jabatanPemroses;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglVerifikasiTahap1;
    private String diVerifikasiOlehTahap1;
    private String jabatanVerifikasiTahap1;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglVerifikasiTahap2;
    private String diVerifikasiOlehTahap2;
    private String jabatanVerifikasiTahap2;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tglPersetujuan;
    private String diSetujuiOleh;
    private String jabatanPenyetuju;
    private String mimeType;
    private String fileName;
    private String hashedFileName;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Audited
    @LastModifiedBy
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Audited
    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}
