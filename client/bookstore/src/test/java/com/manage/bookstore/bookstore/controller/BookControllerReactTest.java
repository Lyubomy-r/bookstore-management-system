package com.manage.bookstore.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.BookRequest;
import com.manage.BookResponse;
import com.manage.bookstore.bookstore.entity.dto.BookDTO;
import com.manage.bookstore.bookstore.exception.GeneralException;
import com.manage.bookstore.bookstore.mapper.BookMapper;
import com.manage.bookstore.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebFluxTest
public class BookControllerReactTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookDTO mockBook;

    private BookDTO mockBookSecond;


    @BeforeEach
    void createBook() {
        mockBook = new BookDTO(UUID.randomUUID(),
                "Mock Book",
                "Mock Author",
                "Mock ISBN",
                20);

        mockBookSecond = new BookDTO(UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                15);
    }


    @Test
    @DisplayName("Test BookControllerReactTest method findAll")
    public void testFindAllBooks() {
        List<BookDTO> list = List.of(mockBook, mockBookSecond);

        when(bookService.findAll()).thenReturn(Flux.fromStream(list.stream()));

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDTO.class)
                .value(List::size, equalTo(2));
    }

    @Test
    @DisplayName("Test BookControllerReactTest method findById")
    void testMethodFindById() {
        String bookId = mockBook.getId().toString();
        String url = "/api/v1/books/" + bookId;
        Mono<BookDTO> bookMono = Mono.just(mockBook);

        when(bookService.findById(bookId)).thenReturn(bookMono);

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDTO.class)
                .value(book -> book.getId().toString(), equalTo(bookId));

        when(bookService.findById(bookId)).thenThrow(new GeneralException("id is empty", HttpStatus.BAD_REQUEST));

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(GeneralException.class);

    }

    @Test
    @DisplayName("Test BookControllerReactTest method saveNewBook")
    void testMethodSaveNewBook() {

        Mono<BookDTO> bookSave = Mono.just(BookDTO.builder()
                .title(mockBook.getTitle())
                .author(mockBook.getAuthor())
                .isbn(mockBook.getIsbn())
                .quantity(mockBook.getQuantity())
                .build());

        Mono<BookDTO> employeeMono = Mono.just(mockBook);
        when(bookService.saveBook(bookSave)).thenReturn(employeeMono);

        webTestClient.post()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookSave), BookDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDTO.class);

    }

    @Test
    @DisplayName("Test BookControllerReactTest method updateBookFields")
    void testMethodUpdateBookFields() throws Exception {
        BookDTO bookUpdate = BookDTO.builder()
                .id(mockBook.getId())
                .quantity(10)
                .build();
        String  bookId = mockBook.getId().toString();
        Mono<BookDTO> employeeMono = Mono.just(mockBook);

        when(bookService.findById(bookId)).thenReturn(employeeMono);
        mockBook.setQuantity(10);
        when(bookService.updateBook(Mono.just(bookUpdate))).thenReturn(employeeMono);

        webTestClient.patch()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookUpdate), BookDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDTO.class);
    }

    @Test
    @DisplayName("Test BookControllerReactTest method deleteBook")
    void testMethodDeleteBook() {
        String bookId = mockBook.getId().toString();
        String url = "/api/v1/books/" + bookId;
        String message = "Employee with id: " + bookId + " is deleted.";
        Mono<String> responseMessageMono = Mono.just(message);

        when(bookService.delete(bookId)).thenReturn(responseMessageMono);

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(message);
    }

}
