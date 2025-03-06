package id.perumdamts.kepegawaian.entities.penggajian;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
        @Index(columnList = "isDeleted"),
        @Index(columnList = "tanggalProses"),
        @Index(columnList = "tanggalVerifikasiTahap1"),
        @Index(columnList = "tanggalVerifikasiTahap2"),
        @Index(columnList = "tanggalPersetujuan"),
        @Index(columnList = "status"),
        @Index(columnList = "periode")
})
@Data
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE gaji_root_batch SET is_deleted=true WHERE batchId=?")
@SQLRestriction("is_deleted = false")
public class GajiBatchRoot implements Serializable {
    @Id
    @Column(name = "batch_id", updatable = false, nullable = false)
    private String id;
    private String periode;
    private Integer status = 0;
    private Integer totalPegawai;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalProses;
    private String diProsesOleh;
    private String jabatanPemroses;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalVerifikasiTahap1;
    private String diVerifikasiOlehTahap1;
    private String jabatanVerifikasiTahap1;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalVerifikasiTahap2;
    private String diVerifikasiOlehTahap2;
    private String jabatanVerifikasiTahap2;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPersetujuan;
    private String diSetujuiOleh;
    private String jabatanPenyetuju;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Audited
    @LastModifiedBy
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Audited
    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}
