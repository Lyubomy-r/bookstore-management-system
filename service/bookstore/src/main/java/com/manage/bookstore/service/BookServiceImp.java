package com.manage.bookstore.service;

import com.google.protobuf.Empty;
import com.manage.BookListResponse;
import com.manage.BookRequest;
import com.manage.BookResponse;
import com.manage.BookServiceGrpc;
import com.manage.GeneralResponse;
import com.manage.bookstore.dao.BookRepository;
import com.manage.bookstore.entity.Book;
import com.manage.bookstore.mapper.BookMapper;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
@Service
public class BookServiceImp extends BookServiceGrpc.BookServiceImplBase {

    Logger logger = LoggerFactory.getLogger(BookServiceImp.class);

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void findAll(Empty request, StreamObserver<BookListResponse> responseObserver) {
        List<Book> bookList = bookRepository.findAll();
        if (!bookList.isEmpty()) {
            BookListResponse.Builder response = BookListResponse.newBuilder();
            bookList.forEach(book -> {
                BookResponse bookResponse = bookMapper.bookToBookResponse(book);
                response.addBooks(bookResponse);
            });
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            logger.info("From BookServiceImp method -findAll- return List of Books.");
        } else {
            logger.warn("From BookServiceImp method -findAll- send war message " +
                    "(Don't find any Book. Books list is empty.)");
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Don't find any Book. Books list is empty.")));
        }
    }

    @Override
    public void findById(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        Optional<Book> book = bookRepository.findById(UUID.fromString(request.getId()));

        if (book.isPresent()) {
            BookResponse response = bookMapper.bookToBookResponse(book.get());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("From BookServiceImp method -findById- return  Book with id: {}", book.get().getId());
        } else {
            logger.warn("From BookServiceImp method -findById- send war message " +
                    "Don't find any Book with this id: {}", request.getId());
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Don't find any Book with this id: " + request.getId())));
        }
    }

    @Override
    public void save(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        if (request == null) {
            logger.warn("From BookServiceImp method -save- send war message " +
                    "Don't find any Book.");
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Invalid Book request.")));
        } else {
            Book newBook = bookMapper.bookRequestToBookWithoutId(request);
            Book saveBook = bookRepository.save(newBook);
            BookResponse response = bookMapper.bookToBookResponse(saveBook);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("From BookServiceImp method -save- Return new save Book from Data Base.");
        }
    }

    @Override
    public void update(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        if (request == null) {
            logger.warn("From BookServiceImp method -update- send war message " +
                    "(Book is not available or his is empty.)");
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Book is not available or his is empty.")));
        }
        Book book = createBookForUpdate(request);
        Book updateBook = bookRepository.save(book);
        BookResponse response = bookMapper.bookToBookResponse(updateBook);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("From BookServiceImp method -updateBook- Return updated Book by id: {}.", updateBook.getId());
    }

    @Override
    public void delete(BookRequest request, StreamObserver<GeneralResponse> responseObserver) {
        Optional<Book> book = bookRepository.findById(UUID.fromString(request.getId()));
        String response = "Book was deleted success.";
        if (book.isPresent()) {
            bookRepository.delete(book.get());

            responseObserver.onNext(GeneralResponse.newBuilder()
                    .setMessage(response)
                    .build());
            responseObserver.onCompleted();
            logger.info("From BookServiceImp method -delete- delete book with id: {}, and return message {}", book.get().getId(), response);
        } else {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Don't find any Book with this id: " + request.getId())));
        }
    }

    private Book createBookForUpdate(BookRequest bookRequest) {
        if (bookRequest != null) {
            Optional<Book> existingBook = bookRepository.findById(UUID.fromString(bookRequest.getId()));
            if (existingBook.isPresent()) {
                if (bookRequest.getTitle() != null && !bookRequest.getTitle().isEmpty()) {
                    existingBook.get().setTitle(bookRequest.getTitle());
                }
                if (bookRequest.getAuthor() != null && !bookRequest.getAuthor().isEmpty()) {
                    existingBook.get().setAuthor(bookRequest.getAuthor());
                }
                if (bookRequest.getIsbn() != null && !bookRequest.getIsbn().isEmpty()) {
                    existingBook.get().setIsbn(bookRequest.getIsbn());
                }
                if (existingBook.get().getQuantity() > bookRequest.getQuantity() || existingBook.get().getQuantity() < bookRequest.getQuantity()) {
                    existingBook.get().setQuantity(bookRequest.getQuantity());
                }

                return existingBook.get();
            } else {
                logger.warn("From BookServiceImp method -createBookForUpdate- send war message " +
                        "(Don't find any Book with this id:  ({}) )", bookRequest.getId());
                throw new RuntimeException("Don't find any Book with this id: " + bookRequest.getId());
            }
        } else {
            logger.warn("From BookServiceImp method -createBookForUpdate- send war message " +
                    "(Book is not available or his is empty.)");
            throw new RuntimeException("Book is not available or his is empty.");
        }
    }
}
