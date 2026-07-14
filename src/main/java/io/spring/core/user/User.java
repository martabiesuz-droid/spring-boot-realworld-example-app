package io.spring.core.user;

import io.spring.Util;
import io.spring.core.AbstractPersistableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(
    of = {"id"},
    callSuper = false)
public class User extends AbstractPersistableEntity {

  @Id private String id;
  private String email;
  private String username;
  private String password;
  private String bio;
  private String image;

  public User(String email, String username, String password, String bio, String image) {
    this.id = UUID.randomUUID().toString();
    this.email = email;
    this.username = username;
    this.password = password;
    this.bio = bio;
    this.image = image;
  }

  public void update(String email, String username, String password, String bio, String image) {
    if (!Util.isEmpty(email)) {
      this.email = email;
    }

    if (!Util.isEmpty(username)) {
      this.username = username;
    }

    if (!Util.isEmpty(password)) {
      this.password = password;
    }

    if (!Util.isEmpty(bio)) {
      this.bio = bio;
    }

    if (!Util.isEmpty(image)) {
      this.image = image;
    }
  }
}
