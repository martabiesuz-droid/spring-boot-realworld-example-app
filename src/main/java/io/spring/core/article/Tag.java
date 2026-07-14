package io.spring.core.article;

import io.spring.core.AbstractPersistableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class Tag extends AbstractPersistableEntity {

  @Id private String id;
  private String name;

  public Tag(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
  }
}
