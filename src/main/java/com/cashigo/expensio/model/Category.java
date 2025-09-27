package com.cashigo.expensio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints =
                @UniqueConstraint(name = "no_duplicate_cat_unq", columnNames = {"name", "userId"}),
        indexes = {
                @Index(name = "category_idx", columnList = "name"),
                @Index(name = "category_userId_idx", columnList = "userId")
        }
)
@Data
@ToString(exclude = "subCategories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private boolean isSystem;

    private String userId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<SubCategory> subCategories;
}
