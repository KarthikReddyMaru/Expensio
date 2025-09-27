package com.cashigo.expensio.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints =
                @UniqueConstraint(name = "no_duplicate_subCat_per_cat_unq", columnNames = {"name", "category_id"}),
        indexes =
                @Index(name = "category_idx", columnList = "category_id")
)
@Data
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private boolean isSystem;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

}
