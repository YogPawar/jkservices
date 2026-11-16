package com.jkservice.work.domain;

import com.jkservice.family.entity.FamilyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "work_types",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_work_type_family",
          columnNames = {"family_id", "name"})
    })
public class WorkTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active = true;
}
