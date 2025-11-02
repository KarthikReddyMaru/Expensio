package com.cashigo.expensio.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import com.cashigo.expensio.common.consts.Status.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(name = "file_status_idx", columnList = "status"),
                @Index(name = "file_expires_idx", columnList = "expires_at")
        }
)
public class FileMetaData {

    @Id
    private String storedFileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    private Instant expiresAt;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;
}
