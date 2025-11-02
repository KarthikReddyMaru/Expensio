package com.cashigo.expensio.model;

import com.cashigo.expensio.common.consts.Status.ImportStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(name = "import_user_id_idx", columnList = "user_id"),
                @Index(name = "import_status_idx", columnList = "status")
        }
)
public class ImportMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Long subCategoriesCreated;

    @Column(nullable = false, updatable = false)
    private Long transactionsSaved;

    @Column(nullable = false, updatable = false)
    private Long transactionsFailed;

    @Column(nullable = false, updatable = false)
    private Long totalRecords;

    @OneToOne(cascade = CascadeType.ALL)
    private FileMetaData errorFile;

    @Column(updatable = false, nullable = false)
    @JsonIgnore
    private String userId;

    @Column(nullable = false, updatable = false)
    private ImportStatus status;
}
