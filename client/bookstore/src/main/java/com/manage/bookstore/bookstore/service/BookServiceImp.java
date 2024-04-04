package com.manage.bookstore.bookstore.service;

import com.google.protobuf.Empty;
import com.manage.BookRequest;
import com.manage.BookServiceGrpc;
import com.manage.bookstore.bookstore.entity.dto.BookDTO;
import com.manage.bookstore.bookstore.exception.GeneralException;
import com.manage.bookstore.bookstore.mapper.BookMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookServiceImp implements BookService {
    Logger logger = LoggerFactory.getLogger(BookServiceImp.class);

    @Autowired
    private BookMapper bookMapper;

    @GrpcClient("grpc-devproblems-service")
    private BookServiceGrpc.BookServiceBlockingStub stub;

    @Override
    public Flux<BookDTO> findAll() {
        return Flux.fromIterable(stub.findAll(Empty.getDefaultInstance()).getBooksList())
                .map(bookResponse -> bookMapper.bookResponseToBookResponseDTO(bookResponse))
                .doOnNext(res -> logger.info("From BookServiceImp method -findAll- Return List of Books."));
    }

    @Override
    public Mono<BookDTO> findById(String bookId) {
        if (bookId.isEmpty()) {
            return Mono.error(new GeneralException("Id is empty.", HttpStatus.BAD_REQUEST));
        }
        BookRequest bookRequest = BookRequest.newBuilder().setId(bookId).build();

        return Mono.fromSupplier(() -> stub.findById(bookRequest))
                .map(stubBook -> bookMapper.bookResponseToBookResponseDTO(stubBook))
                .doOnSuccess(res -> logger.info("From BookServiceImp method -findById- Return Book by id: {} ", res.getId()));
    }

    @Override
    public Mono<BookDTO> saveBook(Mono<BookDTO> newBook) {
        if (newBook == null) {
            logger.warn("From BookServiceImp method -saveBook- send war message " +
                    "(Book is not available or his is empty. ({})))", HttpStatus.BAD_REQUEST);
            return Mono.error(new GeneralException("Book is not available or his is empty.", HttpStatus.BAD_REQUEST));
        }

        return newBook.map(res -> stub.save(bookMapper.bookResponseDTOToBookRequestWithoutId(res)))
                .map(stubBook -> bookMapper.bookResponseToBookResponseDTO(stubBook))
                .doOnSuccess(book -> logger.info("From BookServiceImp method -save- Return new save Book from Data Base."));
    }

    @Override
    public Mono<BookDTO> updateBook(Mono<BookDTO> updateBook) {
        if (updateBook == null) {
            logger.warn("From BookServiceImp method -updateBook- send war message " +
                    "(Book is not available or his is empty. ({})))", HttpStatus.BAD_REQUEST);

            return Mono.error(new GeneralException("Book is not available or his is empty.", HttpStatus.BAD_REQUEST));
        }
        return updateBook.map(book -> stub.update(createBookRequestForUpdate(book)))
                .map(stubRes -> bookMapper.bookResponseToBookResponseDTO(stubRes))
                .doOnSuccess(res -> logger.info("From BookServiceImp method -updateBook- Return updated Book by id: {}.", res.getId()));
    }

    @Override
    public Mono<String> delete(String bookId) {
        if (bookId.isEmpty()) {
            logger.warn("From BookServiceImp method -delete- send war message " +
                    "(Book id is not available or his is empty. ({})))", HttpStatus.NOT_FOUND);
            return Mono.error(new GeneralException("Book id is not available or his is empty.", HttpStatus.NOT_FOUND));
        }
        BookRequest bookRequest = BookRequest.newBuilder()
                .setId(bookId)
                .build();

        return Mono.fromSupplier(() -> stub.delete(bookRequest).getMessage())
                .doOnSuccess(response -> logger.info("From BookServiceImp method -delete- delete book with id: {}, and return message {}", bookId, response));
    }

    private BookRequest createBookRequestForUpdate(BookDTO bookRequest) {
        BookRequest.Builder createBookRequest = BookRequest.newBuilder();
        if (bookRequest != null) {
            if (bookRequest.getId() != null) {
                createBookRequest.setId(bookRequest.getId().toString());
            }
            if (bookRequest.getTitle() != null) {
                createBookRequest.setTitle(bookRequest.getTitle());
            }
            if (bookRequest.getAuthor() != null) {
                createBookRequest.setAuthor(bookRequest.getAuthor());
            }
            if (bookRequest.getIsbn() != null) {
                createBookRequest.setIsbn(bookRequest.getIsbn());
            }
            if (bookRequest.getQuantity() != null) {
                createBookRequest.setQuantity(bookRequest.getQuantity());
            }

            return createBookRequest.build();
        } else {
            logger.warn("From BookServiceImp method -createBookRequestForUpdate- send war message " +
                    "(Book is not available or his is empty.  ({}) )", HttpStatus.BAD_REQUEST);

            throw new GeneralException("Book is not available or his is empty.", HttpStatus.BAD_REQUEST);
        }
    }
}
