package com.manage.bookstore.dao;

import com.manage.bookstore.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class BookRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.name", postgresContainer::getDatabaseName);
    }

    private Book mockBook;
    private Book mockBookSecond;

    @BeforeEach
    void createBook() {
        bookRepository.deleteAll();

        mockBook = new Book(UUID.randomUUID(),
                "Mock Book",
                "Mock Author",
                "Mock ISBN",
                20);

        mockBookSecond = new Book(UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                15);
    }

    @Test
    void connectionEstablished() {
        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
    }

    @Test
    @DisplayName("JUnit grpc test in BookRepositoryTest method findAll books")
    void testMethodsFindAll() {
        bookRepository.saveAll(List.of(mockBook, mockBookSecond));

        List<Book> response = bookRepository.findAll();

        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(response.isEmpty()),
                () -> assertEquals(2, response.size())
        );
    }

    @Test
    @DisplayName("JUnit grpc test in BookRepositoryTest method findById ")
    void testMethodsFindById() {
        mockBook = bookRepository.save(mockBook);

        Optional<Book> response = bookRepository.findById(mockBook.getId());

        assertAll(
                () -> assertNotNull(response),
                () -> assertTrue(response.isPresent()),
                () -> assertEquals(mockBook.getId().toString(), response.get().getId().toString())
        );
    }

    @Test
    @DisplayName("JUnit grpc test in BookRepositoryTest method save ")
    void testMethodsSave() {

        Book response = bookRepository.save(mockBook);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(mockBook.getTitle(), response.getTitle())
        );
    }


    @Test
    @DisplayName("JUnit grpc test in BookRepositoryTest method delete ")
    void testMethodsDelete() {

        mockBook = bookRepository.save(mockBook);

        bookRepository.delete(mockBook);

        Optional<Book> response = bookRepository.findById(mockBook.getId());

        assertTrue(response.isEmpty());
    }
}