package com.manage.bookstore.mapper;

import com.manage.BookRequest;
import com.manage.BookResponse;
import com.manage.bookstore.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    Book bookResponseToBook(BookResponse response);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookResponse bookToBookResponse(Book book);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookRequest bookToBookRequest(Book book);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    Book bookRequestToBook(BookRequest response);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    Book bookRequestToBookWithoutId(BookRequest book);
}
