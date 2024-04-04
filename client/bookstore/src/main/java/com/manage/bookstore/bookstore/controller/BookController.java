package com.manage.bookstore.bookstore.controller;

import com.manage.bookstore.bookstore.entity.dto.BookDTO;
import com.manage.bookstore.bookstore.service.BookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@CrossOrigin(
        allowCredentials = "true",
        origins = {"http://localhost:3000"},
        allowedHeaders = {"Authorization", "Content-Type", "Accept"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE},
        maxAge = 3600L
)
@RestController
@Validated
public class BookController {

    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookServiceImp;

    @GetMapping("/api/v1/books")
    public Flux<BookDTO> findAll() {
        return bookServiceImp.findAll()
                .doOnNext(res -> logger.info("From BookController method -findAll- \"/api/v1/books\". Return List of all Books "));
    }

    @GetMapping("/api/v1/books/{bookId}")
    public Mono<BookDTO> findById(@PathVariable("bookId") String bookId) {
        return bookServiceImp.findById(bookId)
                .doOnSuccess(res -> logger.info("From BookController method -findById- \"/api/v1/books/{bookId}\". Return Book with id: {}", res.getId()));
    }

    @PostMapping("/api/v1/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDTO> saveNewBook(@Valid @RequestBody Mono<BookDTO> bookRequest) {
        Mono<BookDTO> dtoMono = bookServiceImp.saveBook(bookRequest);
        logger.info("From BookController method -saveNewBook- \"/api/v1/books\". Return saved Book ");
        return dtoMono;
    }

    @PatchMapping("/api/v1/books")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookDTO> updateBookFields(@RequestBody Mono<BookDTO> bookRequest) {
        Mono<BookDTO> dtoMono =bookServiceImp.updateBook(bookRequest);
        logger.info("From BookController method -updateBookFields- \"/api/v1/books\". Return updated Book");
        return dtoMono;
    }

    @DeleteMapping("/api/v1/books/{bookId}")
    public Mono<String> deleteBook(@PathVariable("bookId") String bookId) {

        return bookServiceImp.delete(bookId)
                .doOnSuccess(res -> logger.info("From BookController method -deleteBook- \"/api/v1/books/{bookId}\". Delete Book with id: {} and return String message: {}",
                        bookId,
                        res));
    }
}
