package com.manage.bookstore.service;

import com.google.protobuf.Empty;
import com.manage.BookListResponse;
import com.manage.BookRequest;
import com.manage.BookResponse;
import com.manage.BookServiceGrpc;
import com.manage.GeneralResponse;
import com.manage.bookstore.dao.BookRepository;
import com.manage.bookstore.entity.Book;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test-grpc",
        "grpc.server.port=-1",
        "grpc.client.test.address=in-process:test"
})
@DirtiesContext
@Testcontainers
class BookServiceImpGrpcTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private BookRepository bookRepository;

    @GrpcClient("test-grpc")
    private BookServiceGrpc.BookServiceBlockingStub bookServiceBlockingStub;

    private Book mockBook;
    private Book mockBookSecond;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.name", postgresContainer::getDatabaseName);
    }

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
    @DisplayName("JUnit grpc test in BookServiceImp method findAll books")
    void testMethodsFindAll() {
        bookRepository.saveAll(List.of(mockBook, mockBookSecond));

        BookListResponse response = bookServiceBlockingStub.findAll(Empty.getDefaultInstance());

        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(response.getBooksList().isEmpty()),
                () -> assertEquals(2, response.getBooksList().size())
        );
    }

    @Test
    @DisplayName("JUnit grpc test in BookServiceImp method findById ")
    void testMethodsFindById() {
        mockBook = bookRepository.save(mockBook);

        BookResponse response = bookServiceBlockingStub.findById(BookRequest
                .newBuilder()
                .setId(mockBook.getId().toString()).build());

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(mockBook.getId().toString(), response.getId())
        );
    }

    @Test
    @DisplayName("JUnit grpc test in BookServiceImp method save ")
    void testMethodsSave() {
        BookRequest bookRequest = BookRequest
                .newBuilder()
                .setIsbn(mockBook.getIsbn())
                .setAuthor(mockBook.getAuthor())
                .setTitle(mockBook.getTitle())
                .setQuantity(mockBook.getQuantity())
                .build();

        BookResponse response = bookServiceBlockingStub.save(bookRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertFalse(response.getId().isEmpty()),
                () -> assertEquals(mockBook.getTitle(), response.getTitle())
        );
    }

    @Test
    @DisplayName("JUnit grpc test in BookServiceImp method update ")
    void testMethodsUpdate() {
        mockBook = bookRepository.save(mockBook);

        Book mockBookUpdate = new Book(mockBook.getId(),
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                20);

        BookRequest bookRequest = BookRequest
                .newBuilder()
                .setId(mockBook.getId().toString())
                .setIsbn(mockBook.getIsbn())
                .setAuthor(mockBook.getAuthor())
                .setTitle(mockBook.getTitle())
                .setQuantity(mockBookUpdate.getQuantity())
                .build();
        BookResponse response = bookServiceBlockingStub.update(bookRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(mockBook.getId().toString(), response.getId()),
                () -> assertEquals(mockBookUpdate.getQuantity(), response.getQuantity())
        );

    }

    @Test
    @DisplayName("JUnit grpc test in BookServiceImp method delete ")
    void testMethodsDelete() {

        mockBook = bookRepository.save(mockBook);

        BookRequest bookRequest = BookRequest
                .newBuilder()
                .setId(mockBook.getId().toString())
                .build();

        GeneralResponse response = bookServiceBlockingStub.delete(bookRequest);

        assertEquals("Book was deleted success.", response.getMessage());
        Throwable error = assertThrows(RuntimeException.class, () -> bookServiceBlockingStub.findById(bookRequest));

        assertEquals("NOT_FOUND: Don't find any Book with this id: " + bookRequest.getId(), error.getMessage());
    }
}