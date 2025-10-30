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
                @Index(name = "subCat_category_idx", columnList = "category_id")
)
@Data
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(updatable = false)
    private String userId;

    private boolean isSystem;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

}
