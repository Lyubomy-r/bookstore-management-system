package com.manage.bookstore.bookstore.mapper;

import com.manage.BookRequest;
import com.manage.BookResponse;
import com.manage.bookstore.bookstore.entity.dto.BookDTO;
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
    BookDTO bookResponseToBookResponseDTO(BookResponse response);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookResponse bookResponseDTOToBookResponse(BookDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookRequest bookResponseDTOToBookRequest(BookDTO dto);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookRequest bookResponseDTOToBookRequestWithoutId(BookDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "quantity", target = "quantity")
    BookDTO bookRequestToBookResponseDTO(BookRequest response);
}
