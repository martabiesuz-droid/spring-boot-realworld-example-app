package io.spring.infrastructure.user;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class MyBatisUserRepositoryTest {

  @Autowired private UserRepository userRepository;
  private User user;

  @BeforeEach
  public void setUp() {
    String uniq = String.valueOf(System.nanoTime());
    user = new User("aisensiy" + uniq + "@163.com", "aisensiy" + uniq, "123", "", "default");
  }

  @Test
  public void should_save_and_fetch_user_success() {
    userRepository.save(user);
    Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
    Assertions.assertEquals(userOptional.get(), user);
    Optional<User> userOptional2 = userRepository.findByEmail(user.getEmail());
    Assertions.assertEquals(userOptional2.get(), user);
  }

  @Test
  public void should_update_user_success() {
    String newEmail = "newemail@email.com";
    user.update(newEmail, "", "", "", "");
    userRepository.save(user);
    Optional<User> optional = userRepository.findByUsername(user.getUsername());
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get().getEmail(), newEmail);

    String newUsername = "newUsername";
    user.update("", newUsername, "", "", "");
    userRepository.save(user);
    optional = userRepository.findByEmail(user.getEmail());
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get().getUsername(), newUsername);
    Assertions.assertEquals(optional.get().getImage(), user.getImage());
  }

  @Test
  public void should_create_new_user_follow_success() {
    User other = new User("other" + System.nanoTime() + "@example.com", "other" + System.nanoTime(), "123", "", "");
    userRepository.save(other);

    FollowRelation followRelation = new FollowRelation(user.getId(), other.getId());
    userRepository.saveRelation(followRelation);
    Assertions.assertTrue(userRepository.findRelation(user.getId(), other.getId()).isPresent());
  }

  @Test
  public void should_unfollow_user_success() {
    User other = new User("other" + System.nanoTime() + "@example.com", "other" + System.nanoTime(), "123", "", "");
    userRepository.save(other);

    FollowRelation followRelation = new FollowRelation(user.getId(), other.getId());
    userRepository.saveRelation(followRelation);

    userRepository.removeRelation(followRelation);
    Assertions.assertFalse(userRepository.findRelation(user.getId(), other.getId()).isPresent());
  }
}
