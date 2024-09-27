package org.techtask.authservice.unit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.db.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserTest() {
        UserEntity entity = UserEntity.builder()
                .login("testUser")
                .password("password123")
                .build();
        UserEntity savedEntity = userRepository.save(entity);
        assertNotNull(savedEntity.getId());
    }

    @Test
    public void readUsersTest() {
        List<UserEntity> requestList = List.of(
                UserEntity.builder().login("testUser1").password("password1").build(),
                UserEntity.builder().login("testUser2").password("password2").build(),
                UserEntity.builder().login("testUser3").password("password3").build()
        );
        userRepository.saveAll(requestList);
        List<UserEntity> entityList = userRepository.findAll();
        assertEquals(3, entityList.size());
    }

    @Test
    public void findByLoginTest() {
        UserEntity entity = UserEntity.builder()
                .login("testUser")
                .password("password123")
                .build();
        userRepository.save(entity);

        Optional<UserEntity> foundEntity = userRepository.findByLogin("testUser");
        assertTrue(foundEntity.isPresent());
        assertEquals("testUser", foundEntity.get().getLogin());
    }

    @Test
    public void existsByLoginTest() {
        UserEntity entity = UserEntity.builder()
                .login("testUser")
                .password("password123")
                .build();
        userRepository.save(entity);

        Boolean exists = userRepository.existsByLogin("testUser");
        assertTrue(exists);

        exists = userRepository.existsByLogin("nonExistentUser");
        assertFalse(exists);
    }

    @Test
    public void deleteUserTest() {
        UserEntity entity = UserEntity.builder()
                .login("testUser")
                .password("password123")
                .build();
        UserEntity savedEntity = userRepository.save(entity);

        userRepository.deleteById(savedEntity.getId());
        Optional<UserEntity> foundEntity = userRepository.findByLogin("testUser");
        assertFalse(foundEntity.isPresent());
    }
}
