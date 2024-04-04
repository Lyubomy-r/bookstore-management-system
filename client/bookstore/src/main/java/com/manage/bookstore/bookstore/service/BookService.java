package com.manage.bookstore.bookstore.service;

import com.manage.bookstore.bookstore.entity.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface BookService {

    Flux<BookDTO> findAll();

    Mono<BookDTO> findById(String id);

    Mono<BookDTO> saveBook(Mono<BookDTO> newBook) ;

    Mono<BookDTO> updateBook(Mono<BookDTO> updateBook);

    Mono<String> delete(String bookId);

}
